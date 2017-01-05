package urlshortener.zaratech.domain;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

public class VCard {

	private String fName;
	private String name;
	private URI uri;

	public VCard(String name, URI uri) {
		this.fName = name;
		this.name = buildName(name);
		this.uri = uri;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	/**
	 * Returns a string containing the name associated with the VCard in reverse
	 * order and separated by ";".
	 */
	private String buildName(String name) {

		String newName = "";
		String[] nameComponents = getfName().split(" ");

		for (int i = nameComponents.length; i > 0; i--) {
			newName = newName + nameComponents[i - 1] + ";";

		}
		newName = newName.substring(0, newName.length() - 1);

		return newName;
	}

	/**
	 * Returns a string containing the information associated with the VCard in
	 * URL format.
	 */
	public String getEncodedVCard() {

		String start = "BEGIN:VCARD\nVERSION:4.0\n";
		String end = "END:VCARD";
		String vCard = "";
		String fullName = "FN:" + getfName();
		String name = "N:" + getName();
		String uri = "URL:" + getUri().toString();

		// Build VCard
		vCard = start + name + "\n" + fullName + "\n" + uri.toString() + "\n" + end;

		// Encode
		try {
			vCard = URLEncoder.encode(vCard, "UTF-8");
			return vCard;

		} catch (UnsupportedEncodingException e) {
			// VCard encoding failed.
			return null;
		}
	}
}
