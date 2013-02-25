<#assign reviewTermsCondContent = delegator.findOne("Content", Static["org.ofbiz.base.util.UtilMisc"].toMap("contentId", "SP_REVIEW_TERMS_COND"), true) />
<#if ((reviewTermsCondContent.statusId)?if_exists == "CTNT_PUBLISHED")>
    <div class = "writeReviewReviewTermsCondLink">
        <span class="previewBarLink">
            <a name="reviewTermCondLink" id="reviewTermCondLink" href="javascript:displayDialogBox('reviewTermCond_');"><span>${uiLabelMap.TermsConditionsLabel}</span></a>
        </span>
    </div>
${screens.render("component://osafe/widget/DialogScreens.xml#reviewTermConditionDialog")}
</#if>


