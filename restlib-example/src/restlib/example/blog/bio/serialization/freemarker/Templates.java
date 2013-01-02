/*
 * Copyright (C) 2012 David Bordoley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package restlib.example.blog.bio.serialization.freemarker;

import java.io.IOException;

import com.google.common.base.Charsets;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public final class Templates {
    public static final Template ATOM_ENTRY_TMPL;
    public static final Template ATOM_FEED_TMPL;
    public static final Template HTML_ENTRY_TMPL;
    public static final Template HTML_FEED_TMPL;

    static {
        final TemplateLoader tmplLoader = new ClassTemplateLoader(
                Templates.class, "");
        final Configuration cfg = new Configuration();

        cfg.setTemplateLoader(tmplLoader);
        cfg.setDefaultEncoding(Charsets.UTF_8.toString());

        try {
            ATOM_ENTRY_TMPL = cfg.getTemplate("entry.atom.ftl");
            ATOM_FEED_TMPL = cfg.getTemplate("feed.atom.ftl");
            HTML_ENTRY_TMPL = cfg.getTemplate("entry.html.ftl");
            HTML_FEED_TMPL = cfg.getTemplate("feed.html.ftl");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Templates() {
    }
}
