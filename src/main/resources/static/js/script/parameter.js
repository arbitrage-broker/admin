rules = {
    parameterGroupSelect2: "required",
    code: "required",
    title: "required",
    value: "required"
};

messages = {
    parameterGroupSelect2: resources.pleaseSelect.format(resources.parameterGroup),
    code: resources.pleaseEnter.format(resources.code),
    title: resources.pleaseEnter.format(resources.title),
    value: resources.pleaseEnter.format(resources.value)
};

function loadEntityByInput() {
    var entity = {
        id: isNullOrEmpty($("#hdf_id").val()) ? null : $("#hdf_id").val(),
        parameterGroup: isNullOrEmpty($("#parameterGroupSelect2").val()) ? null :  {id: $("#parameterGroupSelect2").val()},
        code: $("#code").val(),
        value: $("#value").val(),
        title: $("#title").val(),
    };
    return entity;
}
function loadSearchEntityByInput() {
    var filter = {
        parameterGroup: isNullOrEmpty($("#parameterGroupSelect2").val()) ? null :  {id: $("#parameterGroupSelect2").val()},
        code: $("#code").val(),
        value: $("#value").val(),
        title: $("#title").val(),
    };
    return filter;
}

columns = [{
    data: 'title'
}, {
    data: 'code'
}, {
    data: 'parameterGroup.title'
}, {
    data: 'value'
}, {
    data: 'modifiedDate',
    render: function (data) {
        return toLocalizingDateString(data)
    }
}, {
    data: 'id',
    searchable: false,
    sortable: false,
    render: function (data) {
        return "<a class='btn btn-default fa fa-pencil' id='" + data + "'></a>"
    }
}];

function loadInputByEntity(entity) {
    $("#hdf_id").val(entity.id);
    $("#parameterGroupSelect2").html("<option value='" + get(() => entity.parameterGroup.id) + "' selected>" + get(() => entity.parameterGroup.title) + "</option>").trigger('change');
    $("#parameterGroupSelect2").val(get(() => entity.parameterGroup.id)).trigger('change');
    $("#parameterGroupSelect2").prop("disabled", true);
    $("#parameterGroup span.input-group-addon").attr("disabled", 'disabled');
    $('#code').val(entity.code).attr('disabled','disabled');
    $("#title").val(entity.title).attr('disabled','disabled');
    $('#value').val(entity.value);

}

function afterSubmitForm() {
    $("#parameterGroupSelect2").prop("disabled", false);
    $("#parameterGroup span.input-group-addon, #code, #title").removeAttr("disabled");
}

function clearParameterForm(){
    clearAll();
    afterSubmitForm();
}
