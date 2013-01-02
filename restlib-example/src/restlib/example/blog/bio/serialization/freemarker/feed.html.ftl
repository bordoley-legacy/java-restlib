<html>
    <head>
        <title>${title}</title>
        <#list links as link>
        <link rel="${link.rel}" <#if link.type??>type="${link.type}" </#if>href="${link.href}">
        </#list>
    </head>
    <body class="hfeed">
        <#list links as link><#if link.rel = "edit">
        <form method="post" action="${link.href}" enctype="application/x-www-form-urlencoded">
            <label>Content: <input type="text" name="content" value=""></label><br>
            <input type="submit" value="Create New Entry">
        </form>         
        </#if></#list>
                
        <#list entries as entry>
        <div id="${entry.id}" class="hentry">
            <h2 class="entry-title">${entry.published}</h2>  
            <div>
                Updated: <abbr class="updated" title="${entry.updated}">${entry.updated}</abbr>
            </div>
            <div>
                Created: <abbr class="published" title="${entry.published}">${entry.published}</abbr>
            </div>
            <p class="entry-content">${entry.content?html}</p>
            <!-- FIXME: This is a little borked. Perhaps if we implement a tag cloud we insert the actual tag URI -->
        </div>
        </#list>
    </body>
</html>