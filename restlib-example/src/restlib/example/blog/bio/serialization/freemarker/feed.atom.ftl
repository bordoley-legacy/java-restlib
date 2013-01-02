<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom"
        xmlns:os="http://a9.com/-/spec/opensearch/1.1/"
        xmlns:app="http://www.w3.org/2007/app">
    <id>${id}</id>
    <updated>${updated}</updated>
    <#list links as link>
    <#include "link.xml.ftl">
    </#list>
        
    <title>${title}</title>
     
    <#list entries as entry>
    <entry>
        <id>${entry.id}</id>
        <title>${entry.published}</title>
        <published>${entry.published}</published>
        <updated>${entry.updated}</updated>

        <content type="html">${entry.content}</content>

        <#list entry.links as link>
        <link rel="${link.rel}" <#if link.type??>type="${link.type}" </#if>href="${link.href}" />
        </#list>
    </entry>
    </#list>
</feed>