package ssinai.scribbler.gui;

public	class PageSize {
	int yPos;
	int height;

	public PageSize (int yPos, int height) {
		this.yPos = yPos;
		this.height = height;
	}

	public int getYPos () {
		return yPos;
	}

	public int getHeight () {
		return height;
	}

	public String toString () {
		return "yPos="+yPos+", height="+height;
	}
}
