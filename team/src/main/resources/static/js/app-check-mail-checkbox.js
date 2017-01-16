$(document).ready(function() {
    $('#mailContainer').hide();
    $('#mailCheckbox').change(
        function () {
            if ($('#mailCheckbox').is(':checked')) {
                $('#mailContainer').show();
            }   
            else {
                $('#mailContainer').hide();
            }
        });
    });
