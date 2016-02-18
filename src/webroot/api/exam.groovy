package webroot.api

import groovy.sql.Sql

def sql = new Sql(application.dataSource)

if('GET' == request.method) {

    def examName = params.examName
    def studentId = params.studentId

    def results = []

    if(studentId) {

        def selectSql = 'SELECT examName, sum(score) as totalScore FROM ExamScoreView WHERE Student_id = :studentId '
        def params = [studentId: studentId]

        if(examName) {
            selectSql += 'AND examName = :examName'
            params << [examName: examName]
        }

        selectSql += ' GROUP BY examName'

        sql.eachRow(selectSql, params) {
            def exam =[
                exam: it.ExamName ,
                totalScore: it.totalScore,
                subjects: []
            ]
            sql.eachRow("SELECT subjectName, score FROM ExamScoreView WHERE student_id = $studentId AND examName = ${it.ExamName} ") {
                exam.subjects << [ subject: it.subjectName, score: it.score ]
            }
            results << exam
        }

    } else {
        sql.eachRow("""
            SELECT distinct name from Exam
        """) {
            results << it.name
        }
    }

    json results
}