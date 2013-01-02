package restlib.serializable.opensearch;

public final class SearchUrlTemplate {
    private SearchUrlTemplate() {}
    /*
    ttemplate      = tscheme ":" thier-part [ "?" tquery ] [ "#" tfragment ]
            tscheme        = *( scheme / tparameter )
            thier-part     = "//" tauthority ( tpath-abempty / tpath-absolute / tpath-rootless / path-empty )
            tauthority     = [ tuserinfo "@" ] thost [ ":" tport ]
            tuserinfo      = *( userinfo / tparameter )
            thost          = *( host / tparameter )
            tport          = *( port / tparameter )
            tpath-abempty  = *( "/" tsegment )
            tsegment       = *( segment / tparameter )
            tpath-absolute = "/" [ tsegment-nz *( "/" tsegment ) ]
            tsegment-nz    = *( segment-nz / tparameter )
            tpath-rootless = tsegment-nz *( "/" tsegment )
            tparameter     = "{" tqname [ tmodifier ] "}"
            tqname         = [ tprefix ":" ] tlname
            tprefix        = *pchar
            tlname         = *pchar
            tmodifier      = "?"
            tquery         = *( query / tparameter )
            tfragement     = *( fragement / tparameter )
            */
}
