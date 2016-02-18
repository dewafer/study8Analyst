angular.module('Controller',['Service', 'ui.grid', 'ui.grid.edit', 'ui.grid.rowEdit', 'ui.grid.cellNav', 'ui.grid.selection'])
    .controller('StudentController', ['$scope', 'StudentService', 'uiGridConstants', '$timeout', '$location', function($scope, StudentService, uiGridConstants, $timeout, $location){
        var self = this;

        self.gridOptions = {

            data: [],

            columnDefs: [
                { name:'id', displayName:'ID', enableCellEdit: false, enableHiding: false },
                { name:'studentNo', displayName:'学号', enableHiding: false },
                { name:'name', displayName:'姓名', enableHiding: false },
                { name:'gender', displayName:'性别', enableHiding: false },
                { name:'class', displayName:'班级', enableHiding: false },
            ],

            rowEditWaitInterval: 700,

            enableRowSelection: true,
            enableSelectAll: true,
            multiSelect: true,

            onRegisterApi: function(gridApi) {
                self.gridApi = gridApi;
                gridApi.rowEdit.on.saveRow($scope, function(rowEntity){
                    var promise = StudentService.put(rowEntity).then(function(){return StudentService.get()}).then(function(response){
                        self.gridOptions.data = response.data;
                    });
                    gridApi.rowEdit.setSavePromise( rowEntity, promise );
                });
            },

        };

        StudentService.get().then(function(response){
            self.gridOptions.data = response.data;
        });

        self.addStudent = function() {
             self.gridOptions.data.unshift({});
             $timeout(function(){self.gridApi.cellNav.scrollToFocus(self.gridOptions.data[0], self.gridOptions.columnDefs[1]);});
        }

        self.removeStudent = function() {
            angular.forEach(self.gridApi.selection.getSelectedRows(), function(rowEntity){
                StudentService.remove(rowEntity.id);
                var indexOf = self.gridOptions.data.indexOf(rowEntity);
                if (indexOf > -1) {
                    self.gridOptions.data.splice(indexOf, 1);
                }
            });
        }

        self.isStudentSelected = function () {
            if(self.gridApi) {
                return self.gridApi.selection.getSelectedCount() > 0;
            } else {
                return false;
            }
        }

        self.analysisStudent = function() {
            var rowEntity = self.gridApi.selection.getSelectedRows()[0];
            $location.path('/student-analysis/' + rowEntity.id);
        }

    }])

    .controller('AnalysisController',['$scope', '$routeParams', 'StudentService', 'ExamService', function($scope, $routeParams, StudentService, ExamService) {
        var self = this;
        self.studentId = $routeParams.id;
        self.detail = null;
        self.ctx = document.querySelector("#myChart").getContext("2d");
        self.chart = new Chart(self.ctx);

        StudentService.get(self.studentId).then(function(response){ self.student = response.data });

        ExamService.query(self.studentId).then(function(response){ self.tests = response.data });

        self.selectTest = function(index) {
            self.detail = self.tests[index];

            var data = {};
            data.labels = _(self.detail.subjects).map('subject').value();
            data.datasets = [{
                fillColor : "rgba(151,187,205,0.5)",
                strokeColor : "rgba(151,187,205,1)",
                pointColor : "rgba(151,187,205,1)",
                pointStrokeColor : "#fff",
                data : _(self.detail.subjects).map('score').value(),
            }];

            self.chart.Radar(data);
        }

    }])
;