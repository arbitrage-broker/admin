rules = {
    userSelect2: "required",
    subscriptionPackageSelect2: "required"
};

messages = {
    userSelect2: resources.pleaseSelect.format(resources.user),
    subscriptionPackageSelect2: resources.pleaseSelect.format(resources.subscriptionPackage)
};

function loadEntityByInput() {
    let model = {
        id: isNullOrEmpty($("#hdf_id").val()) ? null : $("#hdf_id").val(),
        user: isNullOrEmpty($("#userSelect2").val())? null : {id: $("#userSelect2").val()},
        subscriptionPackage: isNullOrEmpty($("#subscriptionPackageSelect2").val())? null : {id:$("#subscriptionPackageSelect2").val()},
        discountPercentage: $("#discountPercentage").val(),
        status: $("#status").val()
    };
    return model;
}
function loadSearchEntityByInput() {
    let filter = {
        userId: isNullOrEmpty($("#userSelect2").val())? null :  $("#userSelect2").val(),
        subscriptionPackageId: isNullOrEmpty($("#subscriptionPackageSelect2").val())? null : $("#subscriptionPackageSelect2").val(),
        discountPercentage: $("#discountPercentage").val(),
        status: $("#status").val()
    };
    return filter;
}

columns = [{
    data: 'user',
    render: function (data) { return "{0} {1}".format(data.firstName, data.lastName) }
},{
    data: 'subscriptionPackage.name'
},{
    data: 'status'
}, {
    data: 'expireDate',
    render: function (data) { return toLocalizingDateString(data, true) }
}, {
    data: 'discountPercentage'
}, {
    data: 'status'
},{
    data: 'finalPrice',
    render: function (data) { return addPeriod(data) }
}, {
    data: 'modifiedDate',
    render: function (data) { return toLocalizingDateString(data, true) }
}, {
    data: 'id',
    searchable: false,
    sortable: false,
    render: function (data) { return "<a class='btn btn-default fa fa-pencil' id='" + data + "'></a> <a class='btn btn-danger fa fa-trash' id='" + data + "'></a>" }
}];

function loadInputByEntity(model) {
    $("#hdf_id").val(model.id);
    $("#userSelect2").html("<option value='" + get(() => model.user.id) + "' selected>" + get(() => "{0} {1}".format(model.user.firstName, model.user.lastName)) + "</option>").trigger('change');
    $("#userSelect2").val(get(() => model.user.id)).trigger('change');
    $("#subscriptionPackageSelect2").html("<option value='" + get(() => model.subscriptionPackage.id) + "' selected>" + model.subscriptionPackage.name + "</option>").trigger('change');
    $("#subscriptionPackageSelect2").val(get(() => model.subscriptionPackage.id)).trigger('change');
    $("#discountPercentage").val(model.discountPercentage);
    $("#status").val(model.status);
    // enableInputs();
}
function onLoad() {
    // $('#userSelect2').on('select2:select', function(e) {
    //     var selectedValue = e.params.data.id;
    //     enableInputs();
    // });
    // disableInputs();
    $("#price").on('input',function () {
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
}
function disableInputs() {
    $("#walletSelect2").prop("disabled", true);
    $("#wallet span.input-group-addon").attr("disabled", 'disabled');
}
function enableInputs() {
    $("#walletSelect2").prop("disabled", false);
    $("#wallet span.input-group-addon").removeAttr("disabled");
}

function clearSubscriptionForm(){
    clearAll();
}