/*
* Copyright 2010-2011 WorldWide Conferencing, LLC
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package reactiverogue.mongodb

import net.liftweb.json.{ Formats, MappingException, Serializer, TypeInfo }
import net.liftweb.json.JsonAST._

import java.util.{ Date, UUID }
import java.util.regex.{ Pattern, PatternSyntaxException }

import reactivemongo.bson.BSONObjectID

import org.joda.time.DateTime

/*
* Provides a way to serialize/de-serialize ObjectIds.
*
* Queries for a BSONObjectID (oid) using the lift-json DSL look like:
* ("_id" -> ("$oid" -> oid.toString))
*/
class ObjectIdSerializer extends Serializer[BSONObjectID] {
  private val ObjectIdClass = classOf[BSONObjectID]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), BSONObjectID] = {
    case (TypeInfo(ObjectIdClass, _), json) => json match {
      case JObject(JField("$oid", JString(s)) :: Nil) if (BSONObjectID.parse(s).isSuccess) =>
        new BSONObjectID(s)
      case x => throw new MappingException("Can't convert " + x + " to BSONObjectID")
    }
  }

  def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case x: BSONObjectID => Meta.objectIdAsJValue(x)
  }
}

/*
* Provides a way to serialize/de-serialize Patterns.
*
* Queries for a Pattern (pattern) using the lift-json DSL look like:
* ("pattern" -> (("$regex" -> pattern.pattern) ~ ("$flags" -> pattern.flags)))
* ("pattern" -> (("$regex" -> "^Mo") ~ ("$flags" -> Pattern.CASE_INSENSITIVE)))
*/
class PatternSerializer extends Serializer[Pattern] {
  private val PatternClass = classOf[Pattern]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Pattern] = {
    case (TypeInfo(PatternClass, _), json) => json match {
      case JObject(JField("$regex", JString(s)) :: JField("$flags", JInt(f)) :: Nil) =>
        Pattern.compile(s, f.intValue)
      case x => throw new MappingException("Can't convert " + x + " to Pattern")
    }
  }

  def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case x: Pattern => Meta.patternAsJValue(x)
  }
}

/*
* Provides a way to serialize/de-serialize Dates.
*
* Queries for a Date (dt) using the lift-json DSL look like:
* ("dt" -> ("$dt" -> formats.dateFormat.format(dt)))
*/
class DateSerializer extends Serializer[Date] {
  private val DateClass = classOf[Date]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), Date] = {
    case (TypeInfo(DateClass, _), json) => json match {
      case JObject(JField("$dt", JString(s)) :: Nil) =>
        format.dateFormat.parse(s).getOrElse(throw new MappingException("Can't parse " + s + " to Date"))
      case x => throw new MappingException("Can't convert " + x + " to Date")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: Date => Meta.dateAsJValue(x, format)
  }
}

/*
* Provides a way to serialize/de-serialize joda time DateTimes.
*
* Queries for a Date (dt) using the lift-json DSL look like:
* ("dt" -> ("$dt" -> formats.dateFormat.format(dt)))
*/
class DateTimeSerializer extends Serializer[DateTime] {
  private val DateTimeClass = classOf[DateTime]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), DateTime] = {
    case (TypeInfo(DateTimeClass, _), json) => json match {
      case JObject(JField("$dt", JString(s)) :: Nil) =>
        new DateTime(format.dateFormat.parse(s).getOrElse(throw new MappingException("Can't parse " + s + " to DateTime")))
      case x => throw new MappingException("Can't convert " + x + " to Date")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: DateTime => Meta.dateAsJValue(x.toDate, format)
  }
}

/*
* Provides a way to serialize/de-serialize UUIDs.
*
* Queries for a UUID (u) using the lift-json DSL look like:
* ("uuid" -> ("$uuid" -> u.toString))
*/
class UUIDSerializer extends Serializer[UUID] {
  private val UUIDClass = classOf[UUID]

  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), UUID] = {
    case (TypeInfo(UUIDClass, _), json) => json match {
      case JObject(JField("$uuid", JString(s)) :: Nil) => UUID.fromString(s)
      case x => throw new MappingException("Can't convert " + x + " to Date")
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case x: UUID => Meta.uuidAsJValue(x)
  }
}
