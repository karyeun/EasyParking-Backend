<%-- 
    Document   : index.jsp
    Created on : Aug 17, 2015, 4:10:31 PM
    Author     : ky.yong
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <script src="js/jquery-1.9.1.js"></script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Challenge</title>
        <script>
            $(function() {
                $.ajaxSetup({ cache: false });
                
                /*
                $.ajax({
                    url:urlReports,
                    type:'post',
                    data: {
                        'reportType':'alarm',
                        'dateFrom':$('#datefrom').val(),
                        'dateTo':$('#dateto').val(),
                        'filter':$.trim($('#lbl').val()),
                        'option':($('#alarmOnly').is(':checked')?1:
                            $('#eventOnly').is(':checked')?2:0)
                    },
                    success: function(pdfDestination) {
                        window.open(pdfDestination);
                        $('#wait').hide();
                        $('#search,#preview').fadeIn('slow', null);
                    },
                    error: function(req, status, err) {
                        alert(status+': '+err);
                        $('#wait').hide();
                        $('#search,#preview').fadeIn('slow', null);
                    }
                });*/                
                
                /*
                    var merchant={
                        'name': merchantName,
                        'address': {
                            'line1':'144,Jln SK3/8',
                            'postcode':'43300',
                            'town':'seri kembangan',
                            'state':'selangor',
                            'country':'malaysia'
                        }
                   };
                   
                   //alert(JSON.stringify(merchant));
                   
                   $.ajax({
                       type:'post',
                       url:'_ah/api/queue/v1/registerMerchant',
                       data:merchant,
                       dataType:'json',
                       success:function(json) {
                           //alert(json.Status);
                           
                           $('#merchantName').val('').focus();
                       },
                       error:function(request,err) {
                           alert(err);
                       }
                   });                
                */
               
               
                $('#test').click(function() {
                    
                    var car={
                        'color':$('#color').val()
                    };
                   
                    alert(JSON.stringify(car));
                    
                    $.ajax({
                        type: 'POST',
                        url: '_ah/api/attapi/v1/att/saveObj',
                        data: car,
                        dataType: 'json',
                        success:function(json) {
                            alert('success: '+json);
                        },
                        error:function(request,err) {
                            alert(err);
                        }
                    });
                    
                });
            });
        </script>        
    </head>
    <body>
        <h1>A&T M2X /w GAE</h1>
        <hr>
        <input type="text" id="color" /><br>
        <button id="test">Save Object /w Objectify()</button>
        
        
    </body>
</html>
