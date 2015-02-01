package com.eyespynature.client;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class TruncatedClickableTextCell extends ClickableTextCell {
	
	private int maxDisplay;

	public TruncatedClickableTextCell(int maxDisplay) {
		this.maxDisplay = maxDisplay;
	}
	
	@Override
	public void render(Context context, SafeHtml value, SafeHtmlBuilder sb) {
		if (value != null) {
			if (value.asString().length() > maxDisplay) {
				String s = value.asString().substring(0, maxDisplay);
				for (int i = 0; i < maxDisplay; i++) {
					sb.append(s.charAt(i));
				}
				for (int i = 0; i < 3; i++) {
					sb.append('.');
				}
			} else {
				sb.append(value);
			}
		}
	}

}
