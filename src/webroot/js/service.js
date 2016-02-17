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
            get: function() {
                return $http.get(api);
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

;