package webroot.api

import groovy.json.JsonSlurper
import groovy.sql.Sql

def sql = new Sql(application.dataSource)

if ('GET' == request.method) {

    if(params.studentId != null) {

        def row = sql.firstRow("""
            SELECT student_id, student_no, name, gender, class FROM Student WHERE student_id = ${params.studentId}
        """)

        json {
            id        row['student_id']
            studentNo row['student_no']
            name      row.name
            gender    row.gender
            'class'   row['class']
        }

    } else {

        def result = [];
        sql.eachRow '''
            SELECT student_id, student_no, name, gender, class FROM Student order by student_id
        ''', { row ->
                result << [
                        id       : row['student_id'],
                        studentNo: row['student_no'],
                        name     : row.name,
                        gender   : row.gender,
                        class    : row['class']
                ]
            }
        json result
    }
}

if ('POST' == request.method) {
    def slurper = new JsonSlurper()
    def data = slurper.parse(request.inputStream)

    def studentId

    sql.withTransaction {
        if (data.id != null) {
            studentId = data.id
            sql.executeUpdate """
                UPDATE Student set student_no = $data.studentNo, name = $data.name, gender = $data.gender, class = ${data['class']}
                WHERE student_id = $studentId
            """
        } else {
            def keys = sql.executeInsert """
                INSERT INTO Student (student_no, name, gender, class) VALUES ($data.studentNo, $data.name, $data.gender, ${data['class']})
            """
            studentId = keys[0][0]
            application.fillExams.call(studentId, sql)
        }

        json {
            response 'OK'
            id studentId
        }
    }
}

if ('DELETE' == request.method) {
    def delCount = 0
    if (params.id) {
        delCount = sql.executeUpdate """
            DELETE FROM Student where student_id = $params.id
        """
    }
    json {
        response 'OK'
        count "$delCount"
    }
}
