package restlib.data;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;

public final class ExtensionMapBuilder {
    private final ImmutableBiMap.Builder<MediaRange, String> mediaRangeExtMap = ImmutableBiMap.builder();
    
    ExtensionMapBuilder(){
    }
    
    public ExtensionMap build() {
        return new ExtensionMap(this.mediaRangeExtMap.build());
    }
    
    public ExtensionMapBuilder put(final MediaRange mediaRange, final String ext) {
        Preconditions.checkNotNull(mediaRange);
        Preconditions.checkNotNull(ext);
        Preconditions.checkArgument(!ext.isEmpty());
        
        this.mediaRangeExtMap.put(mediaRange, ext);
        return this;
    }
}
