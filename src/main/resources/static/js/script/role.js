var detailDataTable;
var detailVersion;
rules = {
    role: "required",
    title: "required"
};

messages = {
    role: resources.pleaseEnter.format(resources.role),
    title: resources.pleaseEnter.format(resources.title)
};

function loadEntityByInput() {
    let model = {
        id: isNullOrEmpty($("#hdf_id").val()) ? null : $("#hdf_id").val(),
        role: $("#role").val(),
        title: $("#title").val()
    };
    return model;
}
function loadSearchEntityByInput() {
    let filter = {
        role: $("#role").val(),
        title: $("#title").val()
    };
    return filter;
}

columns = [{
    data: 'role'
},{
    data: 'title'
}, {
    data: 'modifiedDate',
    render: function (data) { return toLocalizingDateString(data, true) }
},{
    data: 'id',
    searchable: false,
    sortable: false,
    render: function (data, type, row) { return `<a class="btn btn-info fa fa-list-alt" data-toggle="modal" data-target=".bs-example-modal-lg" id="${data}" modal-title="${row.role}" data-bs-toggle="tooltip" title="Detail"></a>` }
}];

function loadInputByEntity(model) {
    $("#hdf_id").val(model.id);
    $('#role').val(model.role);
    $('#title').val(model.title);
}
function onLoad() {
    $.getJSON("/api/v1/common/getEnum/CurrencyType", function (json) {
        if (json.error == null) {
            json.forEach(function (value, index) {
                $("#currency").append("<option value='" + value.id + "'>" + value.text + "</option>");
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

    $(".table:not(#myModal .table)").on("click", ".fa-list-alt", function (e) {
        initModal($(this))
    });
    detailDataTable = initAjaxTable('#myModal .table:eq(0)', [{
        data: 'network'
    },{
        data: 'currency'
    },{
        data: 'address'
    },{
        data: 'modifiedDate',
        render: function (data) { return toLocalizingDateString(data, true) }
    },{
        data: 'id',
        searchable: false,
        sortable: false,
        render: function (data) { return `<a class="btn btn-default fa fa-pencil" id="${data}" data-bs-toggle="tooltip" title="Edit"></a> <a class="btn btn-danger fa fa-trash" id="${data}" data-bs-toggle="tooltip" title="Delete"></a>` }
    }], "/api/v1/role-detail", "loadSearchDetailEntityByInput");
    $.subscribe('reloadDetailTable', detailDataTable.ajax.reload);

    $('#myModal form').validate({
        rules: {
            network: "required",
            currency: "required",
            address: "required"
        },
        messages: {
            network: resources.pleaseSelect.format(resources.network),
            currency: resources.pleaseSelect.format(resources.currency),
            address: resources.pleaseEnter.format(resources.address)
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
                url: '/api/v1/role-detail',
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
        $.getJSON("/api/v1/role-detail/" + $(this).attr("id"), function (entity) {
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
                url: "/api/v1/role-detail/" + $(this).attr("id"),
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
    $("#myModal input:hidden:not(#myModal #hdf_roleId)").val('');
    $('#myModal form')[0].reset();
}
function loadSaveDetailEntityByInput() {
    let entity = {
        id: isNullOrEmpty($("#myModal #hdf_roleDetailId").val()) ? null : $("#myModal #hdf_roleDetailId").val(),
        network: $("#myModal #network").val(),
        currency: $("#myModal #currency").val(),
        address: $("#myModal #address").val(),
        role:isNullOrEmpty($("#myModal #hdf_roleId").val())? null : {id: $("#myModal #hdf_roleId").val()},
    };
    return entity;
}
function loadSearchDetailEntityByInput() {
    let entity = {
        roleId:isNullOrEmpty($("#myModal #hdf_roleId").val())? null :  $("#myModal #hdf_roleId").val(),
    };
    return entity;
}
function loadDetailInputByEntity(model) {
    $("#myModal #hdf_roleDetailId").val(model.id);
    $("#myModal #hdf_roleId").val(model.role.id);
    $("#myModal #network").val(model.network);
    $("#myModal #currency").val(model.currency);
    $("#myModal #address").val(model.address);

}
function initModal(element) {
    $("#myModal #hdf_roleId").val(element.attr('id'));
    $("#myModal .modal-header #myModalLabel small").text(element.attr('modal-title'));
    $.publish("reloadDetailTable");
    $("#myModal .table").DataTable().columns.adjust().responsive.recalc();
}