angular.module('Service',[])
    .factory('StudentService', ['$http', function($http){

        var api = '/api/student.groovy';

        return {
            put: function(data) {
                return $http({
                    method: 'POST',
                    url: api,
                    data: data
                });
            },
            get: function(studentId) {
                if(studentId){
                    return $http.get(api, { params: {studentId: studentId }})
                } else {
                    return $http.get(api);
                }
            },
            remove: function(entityId) {
                return $http({
                    method: 'DELETE',
                    url: api,
                    params: { id:entityId }
                });
            }
        }
    }])

    .factory('ExamService', ['$http', function($http) {

        var api = '/api/exam.groovy';

        return {
            getExams: function() {
                return $http.get(api);
            },
            query: function(studentId, examName) {
                var params = { studentId: studentId };
                if(examName) {
                    params.examName = examName;
                }
                return $http({
                    method: 'GET',
                    url: api,
                    params: params
                });
            },
        }
    }])
;