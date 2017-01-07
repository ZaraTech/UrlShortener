$(document).ready(function() {
    $('#messageContainer').hide();
    $('#vCardCheckbox').change(
        function () {
            if ($('#vCardCheckbox').is(':checked')) {
                $('#messageContainer').show();
            }   
            else {
                $('#messageContainer').hide();
            }
        });
    });
