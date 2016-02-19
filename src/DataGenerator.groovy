import groovy.sql.Sql

import java.util.logging.Level
import java.util.logging.Logger
/**
 * Created by towang on 2/18/2016.
 */

class DataGenerator {
    Sql sql

    def Random rand = new Random()

    def subjects = ['语文', '数学', '英语', '物理', '化学', '政治', '历史', '体育', '美术', '音乐']

    def grades = ['高一': [40, 39, 39], '高二': [45, 44, 44, 43, 42, 41], '高三': [40, 38, 38, 38] ]

    def classes = grades.collectEntries { entry -> (0..<entry.value.size()).collectEntries { [ "${entry.key}（${it + 1}）班" : entry.value[it] ] } }

    def exams = ['摸底考试', '分班考试', '期中考', '期末考', '模拟一', '模拟二', '模拟终', '高考']

    def populate() {

        Logger.getLogger('groovy.sql').level = Level.FINE

        sql.withTransaction {

            int count = sql.firstRow('SELECT count(*) as num FROM Subject').num

            if (count == 0) {
                def insertSubjectSql = 'INSERT INTO Subject(name, sort) VALUES (?, ?)';

                sql.withBatch insertSubjectSql, { ps ->
                    subjects.eachWithIndex { subject, index -> ps.addBatch(subject, index) }
                }
            }

            count = sql.firstRow('SELECT count(*) as num FROM Student').num

            if (count == 0) {
                def insertStudentSql = 'INSERT INTO Student(student_no, name, gender, class) VALUES (?, ?, ?, ?)';

                sql.withBatch insertStudentSql, { ps ->
                    classes.each { String className, int num  ->
                        num.times {
                            ps.addBatch(getStudentNo(), randChineseName(), randGender(), className)
                        }
                    }
                }
            }

            sql.execute('TRUNCATE TABLE Exam')
            fillExams(null, sql)
//            exams.eachWithIndex { examName, index ->
//                def insertExamSql = 'INSERT INTO Exam(student_id, subject_id, name, score, sort)VALUES(?, ?, ?, ?, ?)'
//                sql.eachRow('SELECT student_id FROM Student') { student ->
//                    sql.eachRow('SELECT subject_id FROM Subject') { subject ->
//                        sql.withBatch insertExamSql, { ps ->
//                            ps.addBatch(student['student_id'], subject['subject_id'], examName, randScore(), index)
//                        }
//                    }
//                }
//            }
        }
    }

    def fillExams = { studentId, sql ->

        def sIds = []

        if(studentId != null) {
            sIds << studentId;
        } else {
            sql.eachRow('SELECT student_id FROM Student') { sIds << it.student_id }
        }

        def insertExamSql = 'INSERT INTO Exam(student_id, subject_id, name, score, sort)VALUES(?, ?, ?, ?, ?)'

        exams.eachWithIndex { examName, index ->
            sIds.each { student ->
                sql.eachRow('SELECT subject_id FROM Subject') { subject ->
                    sql.withBatch insertExamSql, { ps ->
                        ps.addBatch(student, subject['subject_id'], examName, randScore(), index)
                    }
                }
            }
        }
    }

    def randChineseName() {
        ChineseCharacterUtils.genGeneralChinese( 2 + rand.nextInt(3))
    }

    def studentNo = 0;
    def getStudentNo() {
        (++ studentNo).toString().padLeft(5, '0')
    }

    def genders = ['男', '女', '未知']
    def randGender() {
        genders[rand.nextInt(genders.size())]
    }

    /**
     * 生成随机分数
     * @return
     */
    def randScore() {
        new GaussianRand(rand).nextScore()
    }


}