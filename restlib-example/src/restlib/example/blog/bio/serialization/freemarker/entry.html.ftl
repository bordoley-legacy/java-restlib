<!DOCTYPE html>
<html>
    <head>
        <title>${published}</title>
        <#list links as link>
        <link rel="${link.rel}" <#if link.type??>type="${link.type}" </#if>href="${link.href}">
        </#list>
    </head>
    <body>
        <div id="${id}" class="hentry">
            <h1 class="entry-title">${published}</h1>        
            <div>
                Updated: <abbr class="updated" title="${updated}">${updated}</abbr>
            </div>
            <div>
                Created: <abbr class="published" title="${published}">${published}</abbr>
            </div>
            <p class="entry-content">${content}</p>
        </div>
    </body>
</html>