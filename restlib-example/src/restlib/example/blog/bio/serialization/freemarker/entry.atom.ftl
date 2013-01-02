<?xml version="1.0" encoding="UTF-8"?>
<entry xmlns="http://www.w3.org/2005/Atom">
    <id>${id}</id>
    <title>${published}</title>
    <published>${published}</published>
    <updated>${updated}</updated>

    <content type="html">${content}</content>

    <#list links as link>
    <#include "link.xml.ftl">
    </#list>
</entry>