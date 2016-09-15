package haruspex.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class TagList implements Iterable<Tag> {
  public enum Context {
    TRACE, ENTITY, BLOCK, POINT
  }
  static final TagList EMPTY = new TagList();
  public static final Tag[] EMPTY_ARRAY = new Tag[0];
  
  private Context context;  
  private final ArrayList<Tag> tags;
  
  
  public static TagList of(Context context, Tag... tags) {
    TagList result = new TagList();
    result.context = context;
    for (int i = 0; i < tags.length; i++) {     
      result.tags.add(tags[i]);
    }
    return result;
  }
  
  public Context getContext() {
    return context;
  }
  
  public TagList() {
    tags = new ArrayList<Tag>();
  }
    
  public TagList addString(String key, String value)  {
    tags.add(Tag.of(key, value));
    return this;
  }

  public TagList addInt(String key, int value)  {
    tags.add(Tag.of(key, new Integer(value)));
    return this;
  }

  public TagList addLong(String key, long value)  {
    tags.add(Tag.of(key, new Long(value)));
    return this;
  }
  
  public TagList addAll(Tag...tags) {
    this.tags.addAll(Arrays.asList(tags));
    return this;
  }

  public TagList prependAll(Tag...tags) {
    this.tags.addAll(0, Arrays.asList(tags));
    return this;
  }

  @Override
  public Iterator<Tag> iterator() {
    return tags.iterator();
  }

  public int size() {
    return tags.size();
  }
  
  public boolean isEmpty() {
    return tags.isEmpty();
  }  
  
}
