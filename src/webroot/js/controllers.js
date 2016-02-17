angular.module('Controller',['Service', 'ui.grid', 'ui.grid.edit', 'ui.grid.rowEdit', 'ui.grid.cellNav', 'ui.grid.selection'])
    .controller('IndexController', ['$scope', 'EntityService', 'uiGridConstants', '$timeout', function($scope, EntityService, uiGridConstants, $timeout){
        var self = this;

        self.gridOptions = {

            data: [],

            columnDefs: [
                { name:'ENTITYID', displayName:'ID', enableCellEdit: false, enableHiding: false },
                { name:'NAME', displayName:'Name', enableHiding: false },
                { name:'ATTRNAME', displayName:'Attribute', enableHiding: false },
                { name:'ATTRVALUE', displayName:'Value', enableHiding: false }
            ],

            enableGridMenu: true,
            gridMenuShowHideColumns: false,
            gridMenuCustomItems: [
                {
                    title: 'Add new row',
                    action: function ($event) {
                      self.gridOptions.data.unshift({});
                      $timeout(function(){self.gridApi.cellNav.scrollToFocus(self.gridOptions.data[0], self.gridOptions.columnDefs[1]);});
                    },
                },
                {
                    title: 'Remove selected row',
                    action: function($event) {
                        angular.forEach(self.gridApi.selection.getSelectedRows(), function(rowEntity){
                            EntityService.remove(rowEntity.ENTITYID);
                            var indexOf = self.gridOptions.data.indexOf(rowEntity);
                            if (indexOf > -1) {
                                self.gridOptions.data.splice(indexOf, 1);
                            }
                        });
                    },
                    shown: function() {
                        return self.gridApi.selection.getSelectedCount() > 0;
                    },
                },
            ],

            rowEditWaitInterval: 700,

            enableRowSelection: true,
            // 不能与cellNav同用
            //enableFullRowSelection: true,
            enableSelectAll: true,
            multiSelect: true,

            onRegisterApi: function(gridApi) {
                self.gridApi = gridApi;
                gridApi.rowEdit.on.saveRow($scope, function(rowEntity){
                    var promise = EntityService.put(rowEntity).then(EntityService.get).then(function(response){
                        self.gridOptions.data = response.data;
                    });
                    gridApi.rowEdit.setSavePromise( rowEntity, promise );
                });
                // refresh row callback
//                gridApi.grid.registerDataChangeCallback(function(){
//                    if(self.gridOptions.data[0] && !self.gridOptions.data[0].ENTITYID) {
//                        self.gridApi.cellNav.scrollToFocus(self.gridOptions.data[0], self.gridOptions.columnDefs[1]);
//                    }
//                }, [uiGridConstants.dataChange.ROW]);
            },

        };

        EntityService.get().then(function(response){
            self.gridOptions.data = response.data;
        });

    }])
;