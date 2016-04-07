<div>
    <div class="b-popup__header">
        <span style="padding-right: 10px;">Permissions</span>
    <span onclick="$.colorbox.close();">
        <img src="${contextPath}/assets/img/icons/b-icon-close.svg" class="b-icon g-right">
    </span>
    </div>
    <div class="b-form__wrapper g-margin-bottom-half">
        <div class="b-workspace__content">
            <div class="b-form__wrapper g-margin-bottom-half">
            <#if relations?? && relations?has_content >
                <table>
                  <thead>
                      <th>Permissions</th>
                      <th>Target</th>
                  </thead>
                    <tbody>
                    <#list relations as r>
                        <tr>
                          <td>
                              ${(r.permissions?seq_contains("Read"))?then("R"," ")}
                              ${(r.permissions?seq_contains("Update"))?then("U"," ")}
                              ${(r.permissions?seq_contains("Write"))?then("W"," ")}
                              ${(r.permissions?seq_contains("Delete"))?then("D"," ")}
                          </td>
                          <td>${r.target.objectId}</td>
                        </tr>
                    </#list>
                    </tbody>
                </table>
            <#else>
                No permissions granted.
            </#if>
            </div>
        </div>
    </div>
</div>