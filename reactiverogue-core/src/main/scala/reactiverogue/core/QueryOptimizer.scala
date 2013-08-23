// Copyright 2011 Foursquare Labs Inc. All Rights Reserved.
package reactiverogue.core

class QueryOptimizer {
  def isEmptyClause(clause: QueryClause[_]): Boolean = clause match {
    case AllQueryClause(_, vs, _) => vs.isEmpty
    case InQueryClause(_, vs, _) => vs.isEmpty
    case EmptyQueryClause(_, _) => true
    case _ => false
  }

  def isEmptyQuery(query: Query[_, _, _]): Boolean = {
    query.condition.clauses.exists(isEmptyClause)
  }

  def isEmptyQuery(query: ModifyQuery[_, _]): Boolean =
    isEmptyQuery(query.query)

  def isEmptyQuery(query: FindAndModifyQuery[_, _]): Boolean =
    isEmptyQuery(query.query)
}
