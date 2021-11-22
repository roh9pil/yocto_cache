DESCRIPTION = "Print Hello World"
PN = 'hellobitbake'
PV = '1'

python do_build() {
	bb.plain("************************");
	bb.plain("*                      *");
	bb.plain("* Hello World          *");
	bb.plain("*                      *");
	bb.plain("************************");
}
