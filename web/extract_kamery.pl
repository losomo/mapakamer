#!/usr/bin/perl
use strict; use warnings;
use XML::LibXML::Reader;
use XML::LibXML::Tools;
use Data::Dump;
use Encode;

my $file = shift @ARGV or die;

my $match = 0;
my $node;
$\="\n";
print <<'EHEAD'
<?xml version='1.0' encoding='UTF-8'?>
<osm version="0.6" generator="V.N.">
EHEAD
;

my $lxt = XML::LibXML::Tools->new();
my $reader = new XML::LibXML::Reader(location => $file)  or die "cannot read $file\n";
while ($reader->read) {
  processNode($reader);
}

sub processNode {
    $reader = shift;

    if ($reader->nodeType == XML_READER_TYPE_ELEMENT && $reader->nodePath =~ m{/node$}) {
        $node = $reader->copyCurrentNode(1);        
    }

    if ($reader->nodePath =~ m{/node/tag$} && $reader->getAttribute('v') eq 'surveillance') {
        $match = 1;
    }

    if ($reader->nodeType == XML_READER_TYPE_END_ELEMENT && $reader->nodePath =~ m{/node$}) {
        if ($match) {
            print encode_utf8($node->toString());
        }
        $lxt->domDelete(node => $node, deleteXPath => '.');
        $match = 0;
    }
}
print '</osm>';
