package webroot.api

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.sql.Sql

def sql = new Sql(application.dataSource)

if ('GET' == request.method) {
    // list entity here
    out << JsonOutput.toJson(sql.dataSet('EntityView').rows())
}

if ('POST' == request.method) {
    def slurper = new JsonSlurper()
    def data = slurper.parse(request.inputStream)

    def entityKey
    def attrKey
    def attrValueKey

    sql.withTransaction {
        if (data.ENTITYID != null) {
            entityKey = data.ENTITYID
            attrKey = data.ATTRIBUTEID
            attrValueKey = data.ENTITYATTRID
            sql.executeUpdate """
                UPDATE Entity set name = $data.NAME, updated = current_timestamp WHERE entityId = $entityKey;
            """
            sql.executeUpdate """
                UPDATE Attribute set name = $data.ATTRNAME, updated = current_timestamp WHERE AttributeId = $attrKey;
            """
            sql.executeUpdate """
                UPDATE EntityAttributeValue set value = $data.ATTRVALUE WHERE EntityAttrId = $attrValueKey;
            """
        } else {
            entityKey = sql.executeInsert("""
                INSERT INTO Entity (name, created, updated) values ($data.NAME, current_timestamp, current_timestamp);
             """)[0][0]
            attrKey = sql.executeInsert("""
                INSERT INTO Attribute (name, created, updated) values ($data.ATTRNAME, current_timestamp, current_timestamp);
            """)[0][0]
            attrValueKey = sql.executeInsert("""
                INSERT INTO EntityAttributeValue (EntityId, AttributeId, value) values ($entityKey, $attrKey, $data.ATTRVALUE);
            """)[0][0]
        }
    }

    json {
        response 'OK'
        keys entityKey, attrKey, attrValueKey
    }
}

if ('DELETE' == request.method) {
    def delCount = 0
    if (params.id) {
        delCount = sql.executeUpdate """
            DELETE FROM Entity where EntityId = $params.id
        """
    }
    json {
        response 'OK'
        count "$delCount"
    }
}

