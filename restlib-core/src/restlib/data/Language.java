package restlib.data;

import java.nio.CharBuffer;
import java.util.Locale;

import javax.annotation.Nullable;

import restlib.impl.CaseInsensitiveString;
import restlib.impl.Parser;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Represents an HTTP Language tag.
 */
public final class Language implements Matcheable<Language> {
    /**
     * The wildcard language "*" that matches any language.
     */
    public static final Language ANY = Language.create("*");
    
    static final Parser<Language> PARSER = new Parser<Language>() {
        @Override
        public Optional<Language> parse(final CharBuffer buffer) {
            Preconditions.checkNotNull(buffer);
            final Optional<String> token = Primitives.TOKEN_PARSER.parse(buffer);
            if (token.isPresent()) {
                return Optional.of(Language.create(token.get()));
            } else {
                return Optional.absent();
            }
        }
    };
    
    /**
     * Returns a new Language instance.
     * @param language a non-null HTTP token.
     * @throws NullPointerException if {@code language} is null.
     * @throws IllegalArgumentException if {@code language} is not a valid HTTP token.
     */
    public static Language create(final String language)  {
        Preconditions.checkNotNull(language);

        Preconditions.checkArgument(Primitives.IS_TOKEN.apply(language));   
        return new Language(CaseInsensitiveString.wrap(language));
    }
    
    /**
     * Returns a new Language instance for the given locale
     * @param locale a non-null locale.
     * @throws NullPointerException if {@code locale} is null.
     */
    public static Language forLocale(final Locale locale) {
        Preconditions.checkNotNull(locale);
        return Language.create(locale.toLanguageTag());
    }
    
    private final CaseInsensitiveString value;
    
    private Language(final CaseInsensitiveString value){
        this.value = value;
    }
    
    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }else if (obj instanceof Language) {
            final Language that = (Language) obj;
            return this.value.equals(that.value);
        }      
        return false;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }
    
    @Override
    public int match(final Language that) {
        Preconditions.checkNotNull(that);
        if (this.equals(that)) {
            return 1000;
        } else if (this.equals(Language.ANY)) {
            return 500;
        }
        return 0;
    }
    
    /**
     * Returns the locale for this Language.
     */
    public Locale toLocale() {
           return Locale.forLanguageTag(this.value.toString());
    }
    
    @Override
    public String toString() {
        return this.value.toString();
    }
}
