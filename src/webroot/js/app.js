angular.module('DemoApp', ['ngRoute', 'Controller'])
    .config(['$routeProvider',
    function($routeProvider){
        $routeProvider
            .when('/', {
                templateUrl: 'template/index.html',
                controller: 'IndexController',
                controllerAs: 'indexCtrl'
            })
            .otherwise({
                redirectTo: '/'
            })
    }])

;