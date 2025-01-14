var detailDataTable;
var detailVersion;
rules = {
    name: "required",
    orderCount: "required",
    price: {
        required: true,
        currency: ["$", false]
    },
    maxPrice: {
        required: true,
        currency: ["$", false]
    },
    withdrawalDurationPerDay: {
        required: true,
        number: true
    },
    userProfitPercentage: {
        required: true,
        number: true
    },
    siteProfitPercentage: {
        required: true,
        number: true
    },
    currency: "required",
    status: "required",
    minTradingReward: {
        required: true,
        number: true,
        decimalPlaces: 4
    },
    maxTradingReward: {
        required: true,
        number: true,
        decimalPlaces: 4
    },
    parentReferralBonus: {
        required: true,
        number: true,
        decimalPlaces: 4
    },
};

messages = {
    name: resources.pleaseEnter.format(resources.name),
    orderCount: resources.pleaseEnter.format(resources.orderCount),
    price: {
        required: resources.pleaseEnter.format(resources.price),
        currency: resources.invalidFormat.format(resources.price)
    },
    maxPrice: {
        required: resources.pleaseEnter.format('{0} {1}'.format(resources.minimum, resources.price)),
        currency: resources.invalidFormat.format('{0} {1}'.format(resources.minimum, resources.price))
    },
    withdrawalDurationPerDay: {
        required: resources.pleaseEnter.format(resources.withdrawalDurationPerDay),
        number: resources.mustBeNumber.format(resources.withdrawalDurationPerDay)
    },
    userProfitPercentage: {
        required: resources.pleaseEnter.format(resources.userProfitPercentage),
        number: resources.mustBeNumber.format(resources.userProfitPercentage)
    },
    siteProfitPercentage: {
        required: resources.pleaseEnter.format(resources.siteProfitPercentage),
        number: resources.mustBeNumber.format(resources.siteProfitPercentage)
    },
    currency: resources.pleaseEnter.format(resources.currency),
    status: resources.pleaseSelect.format(resources.status),
    minTradingReward: {
        required: resources.pleaseEnter.format(resources.minTradingReward),
        number: resources.mustBeNumber.format(resources.minTradingReward),
        decimalPlaces: resources.invalidDecimal.format(4),
    },
    maxTradingReward: {
        required: resources.pleaseEnter.format(resources.maxTradingReward),
        number: resources.mustBeNumber.format(resources.maxTradingReward),
        decimalPlaces: resources.invalidDecimal.format(4),
    },
    parentReferralBonus: {
        required: resources.pleaseEnter.format(resources.parentReferralBonus),
        number: resources.mustBeNumber.format(resources.parentReferralBonus),
        decimalPlaces: resources.invalidDecimal.format(4),
    }
};

function loadEntityByInput() {
    let model = {
        id: isNullOrEmpty($("#hdf_id").val()) ? null : $("#hdf_id").val(),
        duration: $("#duration").val(),
        orderCount: $("#orderCount").val(),
        price: $("#price").val().replace(/,/g, ""),
        maxPrice: $("#maxPrice").val().replace(/,/g, ""),
        currency: isNullOrEmpty($("#currency").val())? null : $("#currency").val(),
        status: isNullOrEmpty($("#status").val()) ? null : $("#status").val(),
        name: $("#name").val(),
        minTradingReward: $("#minTradingReward").val(),
        maxTradingReward: $("#maxTradingReward").val(),
        parentReferralBonus: $("#parentReferralBonus").val(),
        userProfitPercentage: $("#userProfitPercentage").val(),
        siteProfitPercentage: $("#siteProfitPercentage").val(),
        withdrawalDurationPerDay: $("#withdrawalDurationPerDay").val(),
    };
    return model;
}
function loadSearchEntityByInput() {
    let filter = {
        duration: $("#duration").val(),
        orderCount: $("#orderCount").val(),
        price: $("#price").val().replace(/,/g, ""),
        maxPrice: $("#maxPrice").val().replace(/,/g, ""),
        currency: isNullOrEmpty($("#currency").val())? null : $("#currency").val(),
        status: isNullOrEmpty($("#status").val()) ? null : $("#status").val(),
        name: $("#name").val(),
        minTradingReward: $("#minTradingReward").val(),
        maxTradingReward: $("#maxTradingReward").val(),
        parentReferralBonus: $("#parentReferralBonus").val(),
        userProfitPercentage: $("#userProfitPercentage").val(),
        siteProfitPercentage: $("#siteProfitPercentage").val(),
        withdrawalDurationPerDay: $("#withdrawalDurationPerDay").val(),
    };
    return filter;
}

columns = [{
    data: 'name'
},{
    data: 'duration'
}, {
    data: 'orderCount'
}, {
    data: 'price',
    render: function (data) { return addPeriod(data) }
}, {
    data: 'maxPrice',
    render: function (data) { return addPeriod(data) }
}, {
    data: 'currency'
}, {
    data: 'status'
}, {
    data: 'minTradingReward'
},{
    data: 'maxTradingReward'
},{
    data: 'parentReferralBonus'
},{
    data: 'withdrawalDurationPerDay'
},{
    data: 'userProfitPercentage'
},{
    data: 'siteProfitPercentage'
},{
    data: 'modifiedDate',
    render: function (data) { return toLocalizingDateString(data, true) }
},{
    data: 'id',
    searchable: false,
    sortable: false,
    render: function (data, type, row) { return `<a class="btn btn-default fa fa-pencil" id="${data}" data-bs-toggle="tooltip" title="Edit"></a> <a class="btn btn-danger fa fa-trash" id="${data}" data-bs-toggle="tooltip" title="Delete"></a> <a class="btn btn-info fa fa-list-alt" data-toggle="modal" data-target=".bs-example-modal-lg" id="${data}" modal-title="${row.name}" data-bs-toggle="tooltip" title="Detail"></a>` }
}];

function loadInputByEntity(model) {
    $("#hdf_id").val(model.id);
    $("#price").val(addPeriod(model.price));
    $("#maxPrice").val(addPeriod(model.maxPrice));
    $('#currency').val(model.currency);
    $('#name').val(model.name);
    $('#duration').val(model.duration);
    $("#orderCount").val(model.orderCount);
    $("#status").val(model.status);
    $("#minTradingReward").val(model.minTradingReward);
    $("#maxTradingReward").val(model.maxTradingReward);
    $("#description").val(model.description);
    $("#parentReferralBonus").val(model.parentReferralBonus);
    $("#withdrawalDurationPerDay").val(model.withdrawalDurationPerDay);
    $("#userProfitPercentage").val(model.userProfitPercentage);
    $("#siteProfitPercentage").val(model.siteProfitPercentage);

}
function onLoad() {
    $("#price, #maxPrice, #amount").on('input',function () {
        $(this).val(addPeriod($(this).val().replace(/,/g, "")));
    });
    $.getJSON("/api/v1/common/getEnum/CurrencyType", function (json) {
        if (json.error == null) {
            json.forEach(function (value, index) {
                $("#currency").append("<option value='" + value.id + "'>" + value.text + "</option>");
            });
        } else {
            show_error(json.error);
        }
    });
    $.getJSON("/api/v1/common/getEnum/EntityStatusType", function (json) {
        if (json.error == null) {
            json.forEach(function (value, index) {
                $("#status").append("<option value='" + value.id + "'>" + value.text + "</option>");
            });
        } else {
            show_error(json.error);
        }
    });
    $(".table:not(#myModal .table)").on("click", ".fa-list-alt", function (e) {
        initModal($(this))
    });
    detailDataTable = initAjaxTable('#myModal .table:eq(0)', [{
        data: 'amount',
        render: function (data) { return addPeriod(data) }
    },{
        data: 'minProfit'
    },{
        data: 'maxProfit'
    },{
        data: 'modifiedDate',
        render: function (data) { return toLocalizingDateString(data, true) }
    },{
        data: 'id',
        searchable: false,
        sortable: false,
        render: function (data) { return `<a class="btn btn-default fa fa-pencil" id="${data}" data-bs-toggle="tooltip" title="Edit"></a> <a class="btn btn-danger fa fa-trash" id="${data}" data-bs-toggle="tooltip" title="Delete"></a>` }
    }], "/api/v1/subscription-package-detail", "loadSearchDetailEntityByInput");
    $.subscribe('reloadDetailTable', detailDataTable.ajax.reload);

    $('#myModal form').validate({
        rules: {
            minProfit: {
                required: true,
                number: true
            },
            maxProfit: {
                required: true,
                number: true
            },
            amount: {
                required: true,
                currency: ["$", false]
            },
        },
        messages: {
            minProfit: {
                required: resources.pleaseEnter.format(resources.minProfit),
                number: resources.mustBeNumber.format(resources.minProfit)
            },
            maxProfit: {
                required: resources.pleaseEnter.format(resources.maxProfit),
                number: resources.mustBeNumber.format(resources.maxProfit)
            },
            amount: {
                required: resources.pleaseEnter.format(resources.amount),
                currency: resources.invalidFormat.format(resources.amount)
            }
        },
        highlight: function (element) {
            $(element).addClass('is-invalid').closest('div').addClass('bad');
        },
        unhighlight: function (element) {
            $(element).removeClass('is-invalid').closest('div').removeClass('bad');
        },
        errorElement: 'span',
        errorClass: 'invalid-feedback',
        errorPlacement: function (error, element) {
            error.insertAfter(element);
        },
        submitHandler: function (form) {
            $("#myModal .modal-content").block(blockUiOptions());
            let entity = loadSaveDetailEntityByInput();
            entity.version = detailVersion;
            $.ajax({
                type: isNullOrEmpty(entity.id) ? "POST" : "PATCH",
                url: '/api/v1/subscription-package-detail',
                dataType: "json",
                contentType: "application/json;charset=utf-8",
                data: JSON.stringify(entity),
                success: function (data) {
                    $("#myModal .modal-content").unblock();
                    if (data.error == null) {
                        clearAllDetail();
                        show_success(resources.saveSuccess);
                        $.publish('reloadDetailTable');
                    } else {
                        show_error(data.error);
                    }
                },
                error: function (header, status, error) {
                    $("#myModal .modal-content").unblock();
                    if(isNullOrEmpty(get(()=>header.responseJSON)))
                        show_error('ajax answer post returned error: ' + error.responseText);
                    else if(header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                    else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                }
            });
        }
    });
    $("#myModal .table:eq(0)").on("click", ".fa-pencil", function () {
        $.getJSON("/api/v1/subscription-package-detail/" + $(this).attr("id"), function (entity) {
            if (entity.error == null) {
                if (entity.status == 404) {
                    show_warning(resources.nothingFound);
                } else {
                    loadDetailInputByEntity(entity);
                    detailVersion = entity.version;
                }
            } else {
                show_error(entity.error);
            }
        });

    });
    $("#myModal .table").on("click", ".fa-trash", function (e) {
        if (confirm(resources.areYouSure)) {
            $.ajax({
                type: "DELETE",
                url: "/api/v1/subscription-package-detail/" + $(this).attr("id"),
                dataType: "json",
                contentType: "application/json",
                success: function (data) {
                    if (data == undefined) {
                        $.publish("reloadDetailTable");
                        show_success(resources.deleteSuccess);
                    } else {
                        show_error(data.error);
                    }
                },
                error: function (header, status, error) {
                    if(isNullOrEmpty(get(()=>header.responseJSON)))
                        show_error('ajax answer post returned error: ' + error.responseText);
                    else if(header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                    else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                }
            });
        }
    });
}
function clearAllDetail() {
    $("#myModal input:hidden:not(#myModal #hdf_subscriptionPackageId)").val('');
    $('#myModal form')[0].reset();
}
function loadSaveDetailEntityByInput() {
    let entity = {
        id: isNullOrEmpty($("#myModal #hdf_subscriptionPackageDetailId").val()) ? null : $("#myModal #hdf_subscriptionPackageDetailId").val(),
        minProfit: $("#myModal #minProfit").val(),
        maxProfit: $("#myModal #maxProfit").val(),
        amount: $("#myModal #amount").val().replace(/,/g, ""),
        subscriptionPackage:isNullOrEmpty($("#myModal #hdf_subscriptionPackageId").val())? null : {id: $("#myModal #hdf_subscriptionPackageId").val()},
    };
    return entity;
}
function loadSearchDetailEntityByInput() {
    let entity = {
        subscriptionPackageId:isNullOrEmpty($("#myModal #hdf_subscriptionPackageId").val())? null :  $("#myModal #hdf_subscriptionPackageId").val(),
    };
    return entity;
}
function loadDetailInputByEntity(model) {
    $("#myModal #hdf_subscriptionPackageDetailId").val(model.id);
    $("#myModal #hdf_subscriptionPackageId").val(model.subscriptionPackage.id);
    $("#myModal #minProfit").val(addPeriod(model.minProfit));
    $("#myModal #maxProfit").val(addPeriod(model.maxProfit));
    $("#myModal #amount").val(addPeriod(addPeriod(model.amount)));

}
function initModal(element) {
    $("#myModal #hdf_subscriptionPackageId").val(element.attr('id'));
    $("#myModal .modal-header #myModalLabel small").text(element.attr('modal-title'));
    $.publish("reloadDetailTable");
    $("#myModal .table").DataTable().columns.adjust().responsive.recalc();
}