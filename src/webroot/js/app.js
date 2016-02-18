angular.module('DemoApp', ['ngRoute', 'Controller'])
    .config(['$routeProvider',
    function($routeProvider){
        $routeProvider
            .when('/', {
                templateUrl: 'template/student-list.html',
                controller: 'StudentController',
                controllerAs: 'studentCtrl'
            })
            .when('/student-analysis/:id', {
                templateUrl: 'template/student-analysis.html',
                controller: 'AnalysisController',
                controllerAs: 'ctrl'
            })
            .otherwise({
                redirectTo: '/'
            })
    }])

;