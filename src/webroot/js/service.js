angular.module('Service',[])
    .factory('EntityService', ['$http', function($http){
        return {
            put: function(data) {
                return $http({
                    method: 'POST',
                    url: '/api/entity.groovy',
                    data: data
                });
            },
            get: function() {
                return $http.get('/api/entity.groovy');
            },
            remove: function(entityId) {
                return $http({
                    method: 'DELETE',
                    url: '/api/entity.groovy',
                    params: { id:entityId }
                });
            }
        }
    }])

;