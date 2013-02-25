package order;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import org.ofbiz.base.util.UtilValidate;

userLogin = session.getAttribute("userLogin");
orderId = StringUtils.trimToEmpty(parameters.orderId);

orderHeader = null;
OrderNotes = null;

if (UtilValidate.isNotEmpty(orderId)) 
{
	orderHeader = delegator.findByPrimaryKey("OrderHeader", [orderId : orderId]);
	context.orderHeader = orderHeader;
	
	notes = orderHeader.getRelatedOrderBy("OrderHeaderNoteView", ["-noteDateTime"]);
	context.orderNotes = notes;
	orderNotes = notes;

	showNoteHeadingOnPDF = false;
	if (UtilValidate.isNotEmpty(notes) && EntityUtil.filterByCondition(notes, EntityCondition.makeCondition("internalNote", EntityOperator.EQUALS, "N")).size() > 0) 
	{
		showNoteHeadingOnPDF = true;
	}
	context.showNoteHeadingOnPDF = showNoteHeadingOnPDF;
	
	
	// note these are overridden in the OrderViewWebSecure.groovy script if run
	context.hasPermission = true;
	context.canViewInternalDetails = true;

	orderReadHelper = new OrderReadHelper(orderHeader);
	orderItems = orderReadHelper.getOrderItems();
	orderAdjustments = orderReadHelper.getAdjustments();
	orderHeaderAdjustments = orderReadHelper.getOrderHeaderAdjustments();
	orderSubTotal = orderReadHelper.getOrderItemsSubTotal();
	orderTerms = orderHeader.getRelated("OrderTerm");

	context.orderHeader = orderHeader;
	context.orderReadHelper = orderReadHelper;
	context.orderItems = orderItems;
	
	pagingListSize=orderItems.size();
	context.pagingListSize=pagingListSize;
	context.pagingList = orderItems;
}

if(UtilValidate.isNotEmpty(orderId) && security.hasEntityPermission('SPER_ORDER_MGMT', '_VIEW', session))
{
    messageMap=[:];
    messageMap.put("orderId", orderId);

    context.orderId=orderId;
    context.pageTitle = UtilProperties.getMessage("OSafeAdminUiLabels","OrderStatusDetailTitle",messageMap, locale )
    context.generalInfoBoxHeading = UtilProperties.getMessage("OSafeAdminUiLabels","OrderDetailInfoHeading",messageMap, locale )
}
conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("statusTypeId", EntityOperator.EQUALS, "ORDER_STATUS"));
conditions.add(EntityCondition.makeCondition("statusId", EntityOperator.IN, ["ORDER_APPROVED", "ORDER_CANCELLED","ORDER_COMPLETED"]));
mainCond = EntityCondition.makeCondition(conditions, EntityOperator.AND);
statusItems = delegator.findList("StatusItem", mainCond, null, ["sequenceId"], null, false);
context.statusItems = statusItems;
context.notesCount = orderNotes.size();

//is it a store pickup?
storeId = "";
orderDeliveryOptionAttr = orderHeader.getRelatedByAnd("OrderAttribute", [attrName : "DELIVERY_OPTION"]);
orderDeliveryOptionAttr = EntityUtil.getFirst(orderDeliveryOptionAttr);
if (UtilValidate.isNotEmpty(orderDeliveryOptionAttr) && orderDeliveryOptionAttr.attrValue == "STORE_PICKUP") 
{
	context.isStorePickup = "Y";
	orderStoreLocationAttr = orderHeader.getRelatedByAnd("OrderAttribute", [attrName : "STORE_LOCATION"]);
	orderStoreLocationAttr = EntityUtil.getFirst(orderStoreLocationAttr);
	if (UtilValidate.isNotEmpty(orderStoreLocationAttr)) 
	{
		storeId = orderStoreLocationAttr.attrValue;
	}
}