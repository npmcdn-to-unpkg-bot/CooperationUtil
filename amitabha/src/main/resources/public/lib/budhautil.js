/**
 * This function treat the "name" of the form as a prefix, it will change the action to "formName + action"
 * So you have to well design your form name and action name.
 * @param formId
 * @param myAction
 * @returns
 */
function doSubmit(formId, myAction) {
	var form = document.getElementById(formId);
	// "prefix" is reserved by javascript
	var myPrefex = form.name;
	form.action = myPrefex + myAction;

	form.submit();
}

function lessThan(x, y) {
	return (x < y);
}
