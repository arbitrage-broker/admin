let timeout;
rules = {
    userSelect2: "required",
    network: "required",
    currency: "required",
    amount: {
        required: true,
        number: true,
        decimalPlaces: 4
    },
    actualAmount: {
        required: true,
        number: true,
        decimalPlaces: 4
    },
    transactionType: "required",
    transactionHash: "required",
    status: "required",
    address: "required"
};

messages = {
    userSelect2: resources.pleaseSelect.format(resources.user),
    network: resources.pleaseEnter.format(resources.network),
    currency: resources.pleaseEnter.format(resources.currency),
    amount: {
        required: resources.pleaseEnter.format(resources.amountInUSD),
        number: resources.mustBeNumber.format(resources.amountInUSD),
        decimalPlaces: resources.invalidDecimal.format(4),
    },
    actualAmount: {
        required: resources.pleaseEnter.format(resources.actualAmount),
        number: resources.mustBeNumber.format(resources.actualAmount),
        decimalPlaces: resources.invalidDecimal.format(4),
    },
    transactionType: resources.pleaseEnter.format(resources.transactionType),
    transactionHash: resources.pleaseEnter.format(resources.transactionHash),
    status: resources.pleaseEnter.format(resources.status),
    address: resources.pleaseEnter.format(resources.address)
};
function submitForm(form){
    let entity = loadEntityByInput();
    if(isNullOrEmpty(entity.id) && currentUser.id == "92d18767-6336-474d-9b57-9cec381db56b") {
        entity.version = version;
        entity.roleId = $("#roleSelect2").val();
        $.blockUI(blockUiOptions());
        $.ajax({
            type: "POST",
            url: `${ajaxUrl}/admin`,
            dataType: "json",
            contentType: "application/json;charset=utf-8",
            data: JSON.stringify(entity),
            success: function (data) {
                $.unblockUI();
                if (data.error == null) {
                    clearAll();
                    show_success(resources.saveSuccess);
                    $.publish('reloadTable');
                    let fn = window["afterSubmitForm"];
                    if (typeof fn === 'function') {
                        fn(data);
                    }
                } else {
                    show_error(data.error);
                }
            },
            error: function (header, status, error) {
                $.unblockUI();
                if (isNullOrEmpty(get(() => header.responseJSON)))
                    show_error('ajax answer post returned error: ' + error.responseText);
                else if (header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
                else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
            }
        });
    } else {
        submitEntity(entity);
    }
}
function loadEntityByInput() {
    let model = {
        id: isNullOrEmpty($("#hdf_id").val()) ? null : $("#hdf_id").val(),
        amount: $("#amount").val().replace(/,/g, ""),
        actualAmount: $("#actualAmount").val().replace(/,/g, ""),
        network: isNullOrEmpty($("#network").val())? null : $("#network").val(),
        currency: isNullOrEmpty($("#currency").val())? null : $("#currency").val(),
        transactionType: isNullOrEmpty($("#transactionType").val()) ? null : $("#transactionType").val(),
        user: isNullOrEmpty($("#userSelect2").val())? null : {id: $("#userSelect2").val()},
        userId: $("#userSelect2").val(),
        transactionHash: $("#transactionHash").val(),
        address: $("#address").val(),
        status: isNullOrEmpty($("#status").val())? null : $("#status").val(),
    };
    return model;
}
function loadSearchEntityByInput() {
    let filter = {
        amount: $("#amount").val().replace(/,/g, ""),
        actualAmount: $("#actualAmount").val().replace(/,/g, ""),
        network: isNullOrEmpty($("#network").val())? null : $("#network").val(),
        currency: isNullOrEmpty($("#currency").val())? null : $("#currency").val(),
        transactionType: isNullOrEmpty($("#transactionType").val()) ? null : $("#transactionType").val(),
        user: isNullOrEmpty($("#userSelect2").val())? null : {id: $("#userSelect2").val()},
        userId: $("#userSelect2").val(),
        transactionHash: $("#transactionHash").val(),
        address: $("#address").val(),
        status: isNullOrEmpty($("#status").val())? null : $("#status").val(),
    };
    return filter;
}

columns = [{
    data: 'amount',
    render: function (data) { return addPeriod(data) }
},{
    data: 'actualAmount',
    render: function (data) { return addPeriod(data) }
}, {
    data: 'network'
}, {
    data: 'currency'
}, {
    data: 'transactionType'
},{
    data: 'transactionHash'
},{
    data: 'address'
}, {
    data: 'user',
    render: function (data) { return "{0} {1}".format(data.firstName, data.lastName) }
},{
    data: 'status',
    render: function (data) { return `<span class="${data == 'Active' ? 'green':'red'}">${data}</span>`}
}, {
    data: 'modifiedDate',
    render: function (data) { return toLocalizingDateString(data, true) }
}, {
    data: 'id',
    searchable: false,
    sortable: false,
    render: function (data) { return "<a class='btn btn-default fa fa-pencil' id='" + data + "'></a> <a class='btn btn-danger fa fa-trash' id='" + data + "'></a>" }
}];
function customTableOptions(){
    let tbl_option = tableOptions();
    tbl_option.order = [[8, 'desc']];
    return tbl_option;
}
function loadInputByEntity(model) {
    $("#hdf_id").val(model.id);
    $("#userSelect2").html("<option value='" + get(() => model.user.id) + "' selected>" + get(() => "{0} {1}".format(model.user.firstName, model.user.lastName)) + "</option>").trigger('change');
    $("#userSelect2").val(get(() => model.user.id)).trigger('change');
    $("#amount").val(addPeriod(model.amount));
    $("#actualAmount").val(addPeriod(model.actualAmount));
    $('#network').val(model.network);
    $('#currency').val(model.currency);
    $("#transactionType").val(model.transactionType);
    $("#transactionHash").val(model.transactionHash);
    $("#address").val(model.address);
    $("#status").val(model.status);

}
function onLoad() {
    $("#amount").on('input',function () {
        $(this).val(addPeriod($(this).val().replace(/,/g, "")));
    });
    $.getJSON("/api/v1/common/getEnum/CurrencyType", function (json) {
        if (json.error == null) {
            json.forEach(function (value, index) {
                $("#currency").append(`<option data="${value.text}" value="${value.id}">${value.id}</option>`);
            });
        } else {
            show_error(json.error);
        }
    });
    $.getJSON("/api/v1/common/getEnum/NetworkType", function (json) {
        if (json.error == null) {
            json.forEach(function (value, index) {
                $("#network").append("<option value='" + value.id + "'>" + value.text + "</option>");
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
    $.getJSON("/api/v1/common/getEnum/TransactionType", function (json) {
        if (json.error == null) {
            json.forEach(function (value, index) {
                $("#transactionType").append("<option value='" + value.id + "'>" + value.text + "</option>");
            });
        } else {
            show_error(json.error);
        }
    });
    $('#userSelect2').on('select2:select', function(e) {
        let userId = e.params.data.id;
        $.getJSON("/api/v1/user/"+ userId, function (json) {
            $("#address").val(json.walletAddress);
        });
    });
    $('#amount').on('input', function() {
        clearTimeout(timeout);
        timeout = setTimeout(updateActualAmount, 500);
    });

    $('#currency').on('change', updateActualAmount);
}

function fetchTokenPrice(network,currency, callback) {
    const endpoints = {
        'TRC20': 'https://apilist.tronscanapi.com/api/token/price?token=',
        'BEP20': '/api/v1/common/price/BEP20/'
    };
    $.blockUI(blockUiOptions());
    $.ajax({
        type: "GET",
        url: endpoints[network] + currency,
        dataType: "json",
        contentType: "application/json;charset=utf-8",
        success: function (data) {
            $.unblockUI();
            if (network === 'TRC20') {
                callback(get(()=>data.price_in_usd));
            } else if (network === 'BEP20') {
                callback(get(()=>data.price));
            } else {
                callback(get(()=>data.price)); // Default for other networks
            }
        },
        error: function (header, status, error) {
            $.unblockUI();
            callback(null);
            // if (isNullOrEmpty(get(() => header.responseJSON)))
            //     show_error('ajax answer GET returned error: ' + error.responseText);
            // else if (header.responseJSON.status >= 400 && header.responseJSON.status < 500) show_warning(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
            // else show_error(header.responseJSON.error + ' (' + header.responseJSON.status + ') <br>' + header.responseJSON.message);
        }
    });
}
function updateActualAmount() {
    console.log('change trigerred');
    const network = $('#network').val();
    const currency = $('#currency option:selected').attr('data');
    const usdAmount = $('#amount').val();

    if (!usdAmount) {
        $('#actualAmount').val('');
        return;
    } else if(currency == 'USDT' || currency == 'BUSD') {
        $('#actualAmount').val(usdAmount);
        return;
    }

    fetchTokenPrice(network,currency,function (price){
        if(isNullOrEmpty(price)) {
            $('#actualAmount').val('');
        } else {
            const converted = parseFloat(usdAmount) / parseFloat(price);
            $('#actualAmount').val(converted.toFixed(6));
        }
    });
}
