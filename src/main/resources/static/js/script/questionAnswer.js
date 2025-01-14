var detailDataTable;
var detailVersion;
rules = {
    title: "required",
    displayOrder: "required",
    type: "required",
    answerType: "required",
    userSelect2: "required",
    active: "required"
};

messages = {
    title: resources.pleaseEnter.format(resources.title),
    displayOrder: resources.pleaseEnter.format(resources.displayOrder),
    type: resources.pleaseSelect.format(resources.type),
    answerType: resources.pleaseSelect.format(resources.answerType),
    userSelect2: resources.pleaseSelect.format(resources.user),
    active: resources.pleaseSelect.format(resources.status)
};

function loadEntityByInput() {
    let model = {
        id: isNullOrEmpty($("#hdf_id").val()) ? null : $("#hdf_id").val(),
        title: $("#title").val(),
        displayOrder: $("#displayOrder").val(),
        type: $("#type").val(),
        answerType: $("#answerType").val(),
        user: isNullOrEmpty($("#userSelect2").val())? null : {id: $("#userSelect2").val()},
        active: isNullOrEmpty($("#active").val()) ? null : $("#active").val()
    };
    return model;
}

function loadSearchEntityByInput() {
    let filter = {
        title: $("#title").val(),
        displayOrder: $("#displayOrder").val(),
        type: $("#type").val(),
        answerType: $("#answerType").val(),
        userId: isNullOrEmpty($("#userSelect2").val())? null : $("#userSelect2").val(),
        active: isNullOrEmpty($("#active").val()) ? null : $("#active").val()
    };
    return filter;
}

columns = [{
    data: 'title'
},{
    data: 'displayOrder'
}, {
    data: 'type'
}, {
    data: 'answerType'
}, {
    data: 'user',
    render: function (data) { return "{0} {1}".format(data.firstName, data.lastName) }
}, {
    data: 'active'
},{
    data: 'modifiedDate',
    render: function (data) { return toLocalizingDateString(data, true) }
},{
    data: 'id',
    searchable: false,
    sortable: false,
    render: function (data, type, row) { return `<a class="btn btn-default fa fa-pencil" id="${data}" data-bs-toggle="tooltip" title="Edit"></a> <a class="btn btn-danger fa fa-trash" id="${data}" data-bs-toggle="tooltip" title="Delete"></a> <a class="btn btn-info fa fa-list-alt" data-toggle="modal" data-target=".bs-example-modal-lg" id="${data}" modal-title="${row.title}" data-bs-toggle="tooltip" title="${resources.answers}"></a>` }
}];

function loadInputByEntity(model) {
    $("#hdf_id").val(model.id);
    $("#title").val(model.title);
    $("#displayOrder").val(model.displayOrder);
    $('#type').val(model.type);
    $('#answerType').val(model.answerType);
    $("#userSelect2").html("<option value='" + get(() => model.user.id) + "' selected>" + get(() => "{0} {1}".format(model.user.firstName, model.user.lastName)) + "</option>").trigger('change');
    $("#userSelect2").val(get(() => model.user.id)).trigger('change');
    $('#active').val(model.active+'');

}
function onLoad() {
    $.getJSON("/api/v1/common/getEnum/QuestionType", function (json) {
        if (json.error == null) {
            json.forEach(function (value, index) {
                $("#type").append("<option value='" + value.id + "'>" + value.text + "</option>");
            });
        } else {
            show_error(json.error);
        }
    });
    $.getJSON("/api/v1/common/getEnum/AnswerType", function (json) {
        if (json.error == null) {
            json.forEach(function (value, index) {
                $("#answerType").append("<option value='" + value.id + "'>" + value.text + "</option>");
            });
        } else {
            show_error(json.error);
        }
    });
    $(".table:not(#myModal .table)").on("click", ".fa-list-alt", function (e) {
        initModal($(this))
    });
    detailDataTable = initAjaxTable('#myModal .table:eq(0)', [{
        data: 'title'
    },{
        data: 'displayOrder'
    },{
        data: 'active'
    },{
        data: 'modifiedDate',
        render: function (data) { return toLocalizingDateString(data, true) }
    },{
        data: 'id',
        searchable: false,
        sortable: false,
        render: function (data) { return `<a class="btn btn-default fa fa-pencil" id="${data}" data-bs-toggle="tooltip" title="Edit"></a> <a class="btn btn-danger fa fa-trash" id="${data}" data-bs-toggle="tooltip" title="Delete"></a>` }
    }], "/api/v1/answer/findAllTable", "loadSearchDetailEntityByInput");
    $.subscribe('reloadDetailTable', detailDataTable.ajax.reload);

    $('#myModal form').validate({
        rules: {
            answerTitle: "required",
            answerDisplayOrder: "required",
            answerActive: "required"
        },
        messages: {
            answerTitle: resources.pleaseEnter.format(resources.title),
            answerDisplayOrder: resources.pleaseEnter.format(resources.displayOrder),
            answerActive: resources.pleaseSelect.format(resources.status),
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
                url: '/api/v1/answer',
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
        $.getJSON("/api/v1/answer/" + $(this).attr("id"), function (entity) {
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
                url: "/api/v1/answer/" + $(this).attr("id"),
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
    $("#myModal input:hidden:not(#myModal #hdf_masterId)").val('');
    $('#myModal form')[0].reset();
}
function loadSaveDetailEntityByInput() {
    let entity = {
        id: isNullOrEmpty($("#myModal #hdf_detailId").val()) ? null : $("#myModal #hdf_detailId").val(),
        question: {id: $("#hdf_masterId").val()},
        title: $("#myModal #answerTitle").val(),
        displayOrder: $("#myModal #answerDisplayOrder").val(),
        active: $("#myModal #answerActive").val()
    };
    return entity;
}
function loadSearchDetailEntityByInput() {
    let entity = {
        questionId:isNullOrEmpty($("#myModal #hdf_masterId").val())? null :  $("#myModal #hdf_masterId").val(),
    };
    return entity;
}
function loadDetailInputByEntity(model) {
    $("#myModal #hdf_detailId").val(model.id);
    $("#myModal #hdf_masterId").val(model.question.id);
    $("#myModal #answerTitle").val(model.title);
    $("#myModal #answerDisplayOrder").val(model.displayOrder);
    $("#myModal #answerActive").val(model.active+'');

}
function initModal(element) {
    $("#myModal #hdf_masterId").val(element.attr('id'));
    $("#myModal .modal-header #myModalLabel small").text(element.attr('modal-title'));
    $.publish("reloadDetailTable");
}