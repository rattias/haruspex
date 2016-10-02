from ctypes.wintypes import tagSIZE
class ID:
	def __init__(self, value):
		self.value = value
	
	def toLong():
		return self.value
	 
	def _str_():
	 	return str(self.value)

	
class TraceElement:
	def __init__(self, id, *tags):
		self.id = id
		self.tags = tags

	def getTags(self):
		return dict(self.tags)
		 
		 
class Trace(TraceElement):
 	def __init__(self, id, *tags):
 		TraceElement.__init__(self, id, tags)
 		self.id = id
 		self.tags = tagSIZE
 	 	self.entities = []
 	 	
 	def entity(self, name, *tags):
 		entity = Entity(name, tags)
 		self.entities.append(entity)
 		return entity
 	
 