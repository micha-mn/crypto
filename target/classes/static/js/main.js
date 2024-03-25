  
  $( document ).ready(function() {
	   
	     		 var url = "/tablename";
	     		   var source =
                {
                    datatype: "json",
                    datafields: [
                        { name: 'tableName' },
                        { name: 'description' }
                    ],
                    url: url,
                    async: true
                };
                 var dataAdapter = new $.jqx.dataAdapter(source);
                // Create a jqxDropDownList
                $("#jqxWidget").jqxDropDownList({ selectedIndex: 2, source: dataAdapter, displayMember: "description", valueMember: "tableName", width: 200, height: 30,
                });
	     		$("#dateInput").jqxDateTimeInput({ width: '200px', height: '30px' });
	     		
	});
function submitData()
{
	var data={
		"tableName":$("#jqxWidget").val(),
		"referDate":$("#dateInput").val(),
		"value":$("#numberInput").val(),
	}
		$.ajax({
		type: "POST",
		contentType: "application/json",
		url: "/insertData",
		data: JSON.stringify(data),
		dataType: 'json',
		async: true,
		cache: false,
		timeout: 600000,
		success: function(data) {

			alert("sucess");
		},
		error: function(e) {

			console.log("ERROR : ", e);

		}
	});

}		    