package com.example.myapp.model;

public class Message {

	//static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd MMM yyyy HH:mm:ss Z");
															// yyyy-MM-dd'T'HH:mm:ssZ  dd MMM yyyy HH:mm:ss Z
	private String title;
	private String description;
	private String date;
	private String link;
	private String imgLink;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title.trim();
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		String editedDesc = null;

		if(description.contains(">")) {
			String[] parts = description.trim().split(">");
			imgLink = parts[0];
			editedDesc = parts[1];
			setImgLink(imgLink);
		}

		if(editedDesc == null) {
			this.description = description.trim();
		} else {
			this.description = editedDesc;
		}
	}

	public void setImgLink(String imageLink) {
		this.imgLink = imageLink.substring(9);
		System.out.println("imageLink is : " +imgLink);
	}

	public String getImageLink(){
		return imgLink;
	}


	public String getDate() {
		return date; //FORMATTER.format(this.date);
	}

	public void setDate(String date) {
//		try {
//			this.date = FORMATTER.parse(date.trim());
//		} catch (ParseException e) {
//			throw new RuntimeException(e);
//		}
		this.date = date;
	}
	
	public Message copy(){
		Message copy = new Message();
		copy.title = title;
		copy.link = link;
		copy.description = description;
		copy.date = date;
		if(imgLink != null) {
			copy.imgLink = imgLink;
		}
		return copy;
	}


}
