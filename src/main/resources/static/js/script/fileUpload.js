tableOptions.pageLength = 100;

columns = [{
    data: 'name'
},{
    data: 'url',
    searchable: false,
    sortable: false,
    render: function (data, type, row) { return "<img src='" + data + "'></img>" }
}, {
    data: 'modifiedDate',
    render: function (data) { return toLocalizingDateString(data,true) }
}, {
    data: 'name',
    searchable: false,
    sortable: false,
    render: function (data) { return "<a class='btn btn-danger fa fa-trash' id='" + data + "'></a>" }
}];
function loadEntityByInput(isFormData) {
    // if (!isNullOrEmpty(isFormData) && isFormData) {
    //     var formData = new FormData();
    //     formData.append("files", $("#file").get(0).files[0]);
    //     return formData;
    // } else {
    //     return {
    //         name: $("#name").val()
    //     };
    // }
    return {
        name: $("#name").val()
    };
}

function loadSearchEntityByInput() {
    return {
        name: $("#name").val()
    };
}

Dropzone.autoDiscover = false;
function onLoad() {
    let myDropzone = new Dropzone(".dropzone", {
        url: "/api/v1/files",
        init: function () {
            this.on("success", function (file,response) {
                // This event triggers after all files in the queue are processed
                afterDropzoneSubmission(response);
            });
        }
    });
}
function afterDropzoneSubmission(response) {
    $.publish('reloadTable');
    // alert("File uploaded successfully:\n\n" +
    //     "Use this URL into the app for internal usage:\n" + response.url +
    //     "\n\nor\n\nUse this URL to deliver image for external usage:\nhttps://" + window.location.host + response.url);
}
function clearAll_(){
    $('#name').val('');
}