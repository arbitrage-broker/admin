rules = {
    name: "required",
    logo: "required"
};

messages = {
    name: resources.pleaseEnter.format(resources.name),
    logo: resources.pleaseEnter.format(resources.logo)
};

function loadEntityByInput() {
    let model = {
        id: isNullOrEmpty($("#hdf_id").val()) ? null : $("#hdf_id").val(),
        name: $("#name").val(),
        logo: $("#logo").val(),
    };
    return model;
}
function loadSearchEntityByInput() {
    let filter = {
        name: $("#name").val(),
        logo: $("#logo").val(),
    };
    return filter;
}

columns = [{
    data: 'name'
},{
    data: 'logo',
    searchable: false,
    sortable: false,
    render: function (data) { return "<img src='" + data + "'></img>" }
}, {
    data: 'modifiedDate',
    render: function (data) { return toLocalizingDateString(data) }
}, {
    data: 'id',
    searchable: false,
    sortable: false,
    render: function (data) { return "<a class='btn btn-default fa fa-pencil' id='" + data + "'></a> <a class='btn btn-danger fa fa-trash' id='" + data + "'></a>" }
}];

function loadInputByEntity(model) {
    $("#hdf_id").val(model.id);
    $('#name').val(model.name);
    $('#logo').val(model.logo);

}