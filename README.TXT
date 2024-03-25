------------------------------ 
in the Controller add the following :

 private ObjectMapper objectMapper;
	 @Autowired
	    public void setObjectMapper(ObjectMapper objectMapper) {
	        this.objectMapper = objectMapper;
	    }
	 
	 @InitBinder
	 public void initBinder(WebDataBinder binder) {
	     binder.registerCustomEditor(ExportExcelDTO.class, new ExportExcelEditor(objectMapper));
	 }
	 
	@GetMapping("/export")
	public void exportToExcel(@RequestParam ExportExcelDTO exportExcelDTO, HttpServletResponse response) throws IOException {
	     readExcelWriteDBService.exportExcelFile(exportExcelDTO ,response);
    }
    
-----------------------------
in Service add this :
  
  public void exportExcelFile(ExportExcelDTO exportExcelDTO, HttpServletResponse response) {
		
		 System.out.println("from "+exportExcelDTO.getFromPeriod() + " to "+ exportExcelDTO.getToPeriod());
		 // to be replace with the jpa list 
		 DataDTO data = DataDTO.builder().date("12/12/2020").value("123456").build();
		 List<DataDTO> dataList = new ArrayList<>();
		 dataList.add(data);
		 
		 ReadExcelWriteDBUtil.exportExcelFile(dataList ,response);
	}
-----------------------------
in the ReadexcelWriteDbUtil.java add:

public static void exportExcelFile(List<DataDTO> dataList, HttpServletResponse response) {
	// create workbook and sheet
	response.setHeader("Content-Disposition", "attachment; filename=Emkan-IVR-Process-Job-Result-Data-"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm")) +".xlsx");
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    
    try {
    	
    
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("Data");

    // create header row
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("DATE");
    header.createCell(1).setCellValue("VALUE");

    // retrieve data from JPA list and populate rows
   
    int rowNum = 1;
    for (DataDTO data : dataList) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(data.getDate());
        row.createCell(1).setCellValue(data.getValue());
    }

    workbook.write(response.getOutputStream());
    workbook.close();
    } catch (Exception e) {
        e.printStackTrace();
    } 
}
 
--------------------------------------
  add 2 new classES
  - under the DTO package : ExportExcelDTO
  - under the Util package :  ExportExcelEditor
--------------------------------------
in the main.js add the following :


   $("#export").click(function() {
		  const data = {fromPeriod: $("#export-periodOne").val() , toPeriod: $("#export-periodTwo").val()};
		  window.open("/export?exportExcelDTO="+encodeURIComponent(JSON.stringify(data)));
		
		});
		    
------------------------------------
don't forget the secret word that u use "promesory" aw ma ba3ref chu w ktirrrrr 