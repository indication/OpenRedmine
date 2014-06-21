package jp.redmine.redmineclient.form.helper;

interface ConvertToHtmlHelper {
	/**
	 *
	 * @param input TEXT string without reduced text (eg. pre)
	 * @return formatted string
	 */
	String beforeParse(String input);

	/**
	 *
	 * @param input HTML string without reduced text (eg. pre)
	 * @return formatted string
	 */
	String afterParse(String input);
}
