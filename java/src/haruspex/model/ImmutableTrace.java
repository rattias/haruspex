/*
 * Copyright 2016 Roberto Attias
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package haruspex.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

import haruspex.common.ClockDomain;
import haruspex.common.DefaultClockDomain;
import haruspex.common.EventType;
import haruspex.common.GlobalID;
import haruspex.common.ID;
import haruspex.common.LocalID;
import haruspex.common.Tag;
import haruspex.common.TagList;
import haruspex.common.event.ITraceEvent;

public class ImmutableTrace extends ImmutableTraceElementWithID implements ITrace {
  private final ClockDomain clockDomain;
  private final List<ImmutablePoint> points = new ArrayList<>();
  private final HashMap<ID, ImmutableEntity> entities = new HashMap<>();
  private final HashMap<ImmutablePoint, List<Interaction>> interactions = new HashMap<>();
  
  private ImmutableTrace(GlobalID id, Tag...tags) {
    this(id, DefaultClockDomain.INSTANCE, tags);
  }
    
  private ImmutableTrace(GlobalID id, ClockDomain clockDomain, Tag...tags) {
    super(id, null);
    this.clockDomain = clockDomain;
  }
  
  ImmutableEntity entity(String name, Tag...tags) {
    return entity(GlobalID.random(), name, tags);
  }
  
  ImmutableEntity entity(GlobalID id, String name, Tag...tags) {
    ImmutableEntity entity = new ImmutableEntity(this, name, id, tags);
    entities.put(entity.getID(), entity);
    return entity;
  }
  
  private ImmutableEntity entity(GlobalID id) {
    ImmutableEntity entity = new ImmutableEntity(this, id);
    entities.put(entity.getID(), entity);
    return entity;
  }

  int addPoint(ImmutablePoint p) {
    points.add(p);
    return points.size() - 1;
  }

  private static ImmutableTraceElementWithID[] processIDs(ImmutableTrace trace, ID[] ids, ITraceEvent ev) {
    ImmutableEntity en = null;
    ImmutableBlock bl = null;
    
    if (! ids[0].equals(trace.getID())) {
      throw new IllegalArgumentException("event " + ev + " does not belong to trace " + ids[0]);
    }
    ImmutableTraceElement[] stack = new ImmutableTraceElement[ids.length];
    
    stack[0] = trace;
    if (ids.length > 1) {
      en = trace.getEntity((GlobalID)ids[1]);
      if (en == null) {
        en = trace.entity((GlobalID)ids[1]);
      }
      
      if (ids.length > 2) {
        bl = en.getBlock((LocalID)ids[2]);
        if (bl == null) {
          bl = en.block((LocalID)ids[2], TagList.EMPTY_ARRAY, TagList.EMPTY_ARRAY);
        }
        return new ImmutableTraceElementWithID[]{trace, en, bl};
      } else {
        return new ImmutableTraceElementWithID[]{trace, en};
      }
    } else {
      return new ImmutableTraceElementWithID[]{trace};
    }
  }

  void addInteraction(ImmutablePoint cause, ImmutablePoint effect) {
    Interaction inter = new Interaction(cause, effect);
    ImmutablePoint[] mp = new ImmutablePoint[]{cause, effect};
    for (ImmutablePoint p : mp) {
      List<Interaction> inters = interactions.get(p);
      if (inters == null) {
        inters = new ArrayList<Interaction>();
        inters.add(inter);
        interactions.put(p, inters);
      }
      inters.add(inter);
    }
  }
    
  static class EntInfo {
    final ID id;
    int curr, last;
    
    public EntInfo(ID id) {
      this.id = id;
    }
  }

  static class EvRef {
    int idx;
    final EntInfo ent;
    
    public EvRef(EntInfo ent, int idx) {
      this.ent = ent;
      this.idx = idx;
    }
  }
  
  static class EvInteraction {
    EvRef cause;
    ImmutablePoint causePoint;
    List<EvRef> effects = new ArrayList<>();
    List<ImmutablePoint> effectPoints = new ArrayList<>();
  }

  private static void sort(List<ITraceEvent> events) {
    events.sort(new Comparator<ITraceEvent>() {
      @Override
      public int compare(ITraceEvent o1, ITraceEvent o2) {
        ID ent1ID = o1.getEntityId();
        ID ent2ID = o2.getEntityId();
        if (ent1ID != null && ent2ID != null) {
          long e1IDlong = ent1ID.toLong();
          long e2IDlong = ent2ID.toLong();
          if (e1IDlong == e2IDlong) {
            return Long.compare(o1.getSeqNum(), o2.getSeqNum());
          } else {
            return Long.compare(e1IDlong, e2IDlong);
          }
        }
        if (ent1ID == null && ent2ID == null) {
          return Integer.compare(o1.hashCode(),  o2.hashCode());
        }
        return (ent1ID == null) ? -1 : +1;
      }     
    });
  }

  /**
   * return the point at the specified index from a topologically-oredered list
   * of points for the entire trace.
   * @param idx index of the point
   * @return the point at the specified index
   */
  public ImmutablePoint getPointAt(int idx) {
    return points.get(idx);
  }

  /**
   * returns an iterator on all entitites in this trace.
   * @return an Iterator on all entitites
   */
  public Iterator<ImmutableEntity> getEntityIterator() {
    return entities.values().iterator();
  }

  /**
   * returns the number of entities in this trace.
   * @return the number of entitites
   */
  @Override
  public int getEntityCount() {
    return entities.size();
  }

  /**
   * returns the entity with the specified ID.
   * 
   */
  @Override
  public ImmutableEntity getEntity(ID id) {
    return entities.get(id);
  }


  /**
   * returns the ClockDomain associated to this trace
   * @return the clock domain
   */
  @Override
  public ClockDomain getClockDomain() {
    return clockDomain;
  }
  
  /**
   * returns the list of effect points in the interaction having the specified point as cause.
   * @param cause the cause point 
   * @return the list of effect points
   */
  public List<ImmutablePoint> getEffects(ImmutablePoint cause) {
    List<ImmutablePoint> result = new ArrayList<>();
    List<Interaction> inters = interactions.get(cause);
    if (inters != null) {
      for (Interaction inter : inters) {
        if (inter.getCause() == cause) {
          result.add(inter.getEffect());
        }
      }
    }
    return result;
  }
  
  /**
   * returns the cause point in the interaction having the specified point as effect.
   * @param effect the effect point 
   * @return the cause point
   */
  public ImmutablePoint getCause(ImmutablePoint effect) {
    List<Interaction> inters = interactions.get(effect);
    if (inters != null) {
      for (Interaction inter : inters) {
        if (inter.getEffect() == effect) {
          return inter.getCause();
        }
      }
    }
    return null;
  }
  
  /**
   * returns an iterator on all Interactions in this trace, where an Interaction is a cause-effect
   * pair of points. Note that multiple interactions with the same cause may be returned.
   * 
   * @return the interactions.
   */
  
  public Iterator<Interaction> getInteractionIterator() {
    return new Iterator<Interaction>() {
      private Iterator<List<Interaction>> it = interactions.values().iterator();
      private List<Interaction> nextList = it.hasNext() ? it.next() : null;
      private int idx = 0;
      
      @Override
      public boolean hasNext() {
        return (nextList != null); 
      }

      @Override
      public Interaction next() {
        if (nextList == null) {
          throw new NoSuchElementException();
        }
        Interaction res = nextList.get(idx);
        idx++;
        if (idx >= nextList.size()) {
          nextList = it.hasNext() ? it.next() : null;
        }
        return res;
      }      
    };
  }
  
  public static ImmutableTrace fromEvents(List<ITraceEvent> events) {
    // sort by seqnum if in same entity, by entity if in different entities,
    // event belonging to trace but no entity go first, sorted by hashCode
    sort(events);
    
    System.out.println("AFTER SORT:");
    int idx = 0;
    for (ITraceEvent ev : events) {
      System.out.println(idx + ": " + ev);
      idx++;
    }

    // scan, collect Interactions and find entity initial events
    HashMap<ID, EntInfo> entID2EntInfo = new HashMap<>();
    HashMap<String, EvInteraction> intersById = new HashMap<>();
    
    for (int i = 0; i < events.size(); i++) {
      ITraceEvent ev = events.get(i);
      ID entId = ev.getEntityId();
      if (entId != null) {
        EntInfo entInfo = entID2EntInfo.get(entId);
        if (entInfo == null) {
          entInfo = new EntInfo(entId);
          entInfo.curr = i;
          entID2EntInfo.put(entId, entInfo);
        } else {
          entInfo.last = i;
        }
        String causeId  = ev.getCauseIdAsString();
        String effectId  = ev.getEffectIdAsString();
        if (causeId != null) {
          EvInteraction inter = intersById.get(causeId);
          if (inter == null) {
            inter = new EvInteraction();
            inter.cause = new EvRef(entInfo, i);
            intersById.put(causeId, inter);
          }
        }
        if (effectId != null) {
          EvInteraction inter = intersById.get(effectId);
          if (inter == null) {
            inter = new EvInteraction();
            intersById.put(effectId, inter);
          }
          inter.effects.add(new EvRef(entInfo, i));
        }
      }
    }

    // create trace, annotate it
    ImmutableTrace trace = new ImmutableTrace((GlobalID)events.get(0).getIDs()[0]);
    for (ITraceEvent ev : events) {
      if (ev.getEntityId() != null) {
        break;
      }
      trace.addAll(ev.getTagLists());
    }
    
    // TOPOLOGICAL SORTING
    // 1) create initial set
    List<EntInfo> candidates = new ArrayList<>();
    for (EntInfo ei : entID2EntInfo.values()) {
      ITraceEvent ev = events.get(0);
      String effectId = ev.getEffectIdAsString();
      if (effectId == null) {
        candidates.add(ei);
      }
    }
    if (candidates.isEmpty()) {
      throw new Error();
    }
    while (! candidates.isEmpty()) {
      // select a candidate, create point for it in resulting trace
      // TODO: use ClockDomain do pick a candidate if there is more than one
      EntInfo ei = candidates.get(0);
//      System.out.println("selected " + ei.id + ", " + ei.curr + ", " + ei.last);
      ITraceEvent ev = events.get(ei.curr);
      
      // make next event in entity the current one
      ei.curr++;
      
      // if such event is an effect and the cause hasn't been added yet, remove
      // evRef from candidates.
      if (ei.curr <= ei.last) {
        ITraceEvent nev = events.get(ei.curr);
        String effectId = nev.getEffectIdAsString();
        if (effectId != null) {
          EvInteraction evInt = intersById.get(effectId);
          if (evInt.cause != null && evInt.causePoint == null) {
            candidates.remove(ei);            
          }
        }
      } else {
        candidates.remove(ei);        
      }
      
      ImmutableTraceElementWithID[] els = processIDs(trace, ev.getIDs(), ev);      
      TagList[] tagLists = ev.getTagLists();
      ImmutablePoint pointInContext = null;
      for (TagList tl : tagLists) {
        Tag type = tl.getEventType();
        switch (EventType.forTag(type)) {
        case BEGIN_TRACE:
        case ANNOTATE_TRACE:
        case END_TRACE:
          els[0].addAll(tl);
          break;
        case BEGIN_ENTITY:
        case ANNOTATE_ENTITY:
        case END_ENTITY:
          els[1].addAll(tl);
          break;
        case BEGIN_BLOCK:
          pointInContext = ((ImmutableBlock)els[2]).getBegin();
          els[2].addAll(tl);
          break;
        case ANNOTATE_BLOCK:
          els[2].addAll(tl);
          break;
        case END_BLOCK:
          ((ImmutableBlock)els[2]).close(tl.toTagArray());
          pointInContext = ((ImmutableBlock)els[2]).getEnd();
          els[2].addAll(tl);
          break;
        case ANNOTATE_POINT:
          if (pointInContext == null) {
            pointInContext = ((ImmutableBlock)els[2]).point(ev.getTime(), tl.toTagArray());
          }
          pointInContext.addAll(tl);
          // if we processed a point event which is a cause, and the effect is the next
          // event for the entity, then add that to the candidates:
          String id = ev.getCauseIdAsString();
          if (id != null) {
            EvInteraction inter = intersById.get(id);
            inter.causePoint = pointInContext;
            for (EvRef effect : inter.effects) {
              if (effect.ent.curr == effect.idx) {
                candidates.add(effect.ent);
              }
            }
          }          
          id = ev.getEffectIdAsString();
          if (id != null) {
            EvInteraction inter = intersById.get(id);
            if (inter != null) {
              inter.effectPoints.add(pointInContext);
            }
          }
          break;
        }
        els[0].remove(type.getKey());
      }
    }
    for (EntInfo ei : entID2EntInfo.values()) {
      if (ei.curr != ei.last + 1) {
        throw new IllegalStateException("Entity "+ ei.id + " in invalid state (" + ei.curr + ")");
      }
    }
    // add collected interactions to trace
    for (EvInteraction evInt : intersById.values()) {
      for (ImmutablePoint p : evInt.effectPoints) {
        trace.addInteraction(evInt.causePoint, p);
      }
    }
    return trace;
  }
}

