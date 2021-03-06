/*
	File: Rom.java see AnimateMK14.java - Science Of Cambridge MK-14 emulator written in Java.

	Doug Rice 2018, using C code by Paul Robson and others.

*/




/********************************************************************/
/*							ROM (SCIOS) Image						*/
/********************************************************************/

/*
unsigned char ROMImage[] =
*/

class Rom {

int ROMImageV2[] =
{/*
 *	 
 * version 2 of monitor 0000 00
 * usage:
 * Z 0 F 2 0  M C 4 M 0 7 M 0 7 M 3 F Z 0 F 2 0 G
 * 
 */
 
0,207,255,144,30,55,194,12,51,199,255,192,242,1,192,235,49,192,231,53,192,
231,50,192,227,54,192,228,0,7,192,222,8,5,63,200,217,64,200,215,6,
200,213,53,200,204,49,200,202,196,15,54,200,198,196,0,50,200,194,199,1,
51,202,12,55,202,14,196,0,202,2,202,3,196,1,55,144,109,194,14,144,
179,197,1,1,196,1,203,213,196,1,7,143,8,195,213,80,152,7,143,24,
196,0,7,144,5,196,0,7,143,24,143,32,195,213,243,213,156,224,187,214,
156,215,63,196,8,203,213,6,212,32,152,251,143,28,25,143,28,187,213,156,
242,64,205,1,144,233,198,254,50,3,251,216,201,1,63,8,170,14,144,54,
194,14,53,194,12,49,194,13,201,0,144,52,228,6,152,157,228,5,152,34,
170,12,156,30,144,226,196,255,202,17,202,15,194,14,53,194,12,49,193,0,
202,13,196,63,51,63,144,220,196,26,51,63,144,234,196,255,202,15,194,14,
53,194,12,49,193,0,202,13,196,63,51,63,144,194,196,4,202,9,170,15,
156,6,196,0,202,13,202,17,2,194,13,242,13,202,13,186,9,156,245,194,
13,88,202,13,144,150,63,6,91,79,102,109,125,7,127,103,119,124,57,94,
121,113,196,4,202,9,170,15,156,6,196,0,202,14,202,12,2,194,12,242,
12,202,12,194,14,242,14,202,14,186,9,156,239,194,12,88,202,12,63,196,
1,53,196,11,49,194,13,212,15,1,193,128,202,0,194,13,28,28,28,28,
1,193,128,202,1,3,196,1,53,196,11,49,194,12,212,15,1,193,128,202,
4,194,12,28,28,28,28,1,193,128,202,5,6,212,128,152,9,2,196,0,
202,3,198,2,144,222,198,254,196,0,202,11,196,13,53,196,255,202,16,196,
10,202,9,196,0,202,10,49,170,16,1,194,128,201,128,143,0,193,128,228,
255,156,76,186,9,156,237,194,10,152,10,194,11,156,216,194,10,202,11,144,
210,194,11,152,206,1,64,212,32,156,40,196,128,80,156,27,196,64,80,156,
25,196,15,80,244,7,1,192,128,1,199,2,63,144,169,10,11,12,13,0,
0,14,15,96,144,239,96,244,8,144,234,96,228,4,152,8,63,144,145,88,
202,10,144,175,196,0,55,196,75,51,63
};

int ROMImageV1[] =

/*
 * 
 * 	version 1 of monitor ---- --
 * usage:
 * Z M 0 F 2 0  T C 4 T M T 0 7 T M T 0 7 T M T 3 F T Z G 0 F 2 0 T
 * 
 *   
 */
{ 
 0x08,0x90,0x1D,0xC2,0x0E,0x37,0xC2,0x0C,0x33,0xC7,0xFF,0xC0,0xF2,0x01,0xC0,0xEB
,0x31,0xC0,0xE7,0x35,0xC0,0xE7,0x32,0xC0,0xE3,0x36,0xC0,0xE4,0x07,0xC0,0xDF,0x3F
,0xC8,0xDC,0x40,0xC8,0xDA,0x06,0xC8,0xD8,0x35,0xC8,0xCF,0x31,0xC8,0xCD,0xC4,0x0F
,0x36,0xC8,0xC9,0xC4,0x00,0x32,0xC8,0xC5,0xC7,0x01,0x33,0xCA,0x0C,0x37,0xCA,0x0E
,0xC4,0x00,0xCA,0x02,0xCA,0x03,0x08,0x08,0xC4,0x40,0xCA,0x00,0xCA,0x01,0xCA,0x04
,0xCA,0x05,0xCA,0x06,0xCA,0x07,0xC4,0x01,0x37,0xC4,0x84,0x33,0x3F,0x90,0x02,0x90
,0xDF,0xE4,0x07,0x98,0x56,0xE4,0x01,0x9C,0xD7,0xC4,0xFF,0xCA,0x0F,0xC4,0x40,0xCA
,0x00,0xCA,0x01,0xC4,0x59,0x33,0x3F,0x90,0x06,0xC4,0x1A,0x33,0x3F,0x90,0xF4,0xE4
,0x03,0x98,0x80,0xC4,0x79,0xCA,0x07,0xC4,0x50,0xCA,0x06,0xCA,0x05,0xCA,0x03,0xC4
,0x5C,0xCA,0x04,0xC4,0x00,0xCA,0x02,0xCA,0x01,0xCA,0x00,0x90,0xB9,0xC2,0x11,0x9C
,0x36,0xC2,0x0E,0x35,0xC2,0x0C,0x31,0xC2,0x0D,0xC9,0x00,0x90,0x0E,0xE4,0x06,0x98
,0xD2,0xE4,0x05,0x98,0xE8,0xAA,0x0C,0x9C,0x02,0xAA,0x0E,0xC4,0xFF,0xCA,0x11,0xCA
,0x0F,0xC2,0x0E,0x35,0xC2,0x0C,0x31,0xC1,0x00,0xCA,0x0D,0xC4,0x3F,0x33,0x3F,0x90
,0xDC,0xC4,0x1A,0x33,0x3F,0x90,0xEA,0xC4,0xFF,0xCA,0x0F,0xC2,0x0E,0x35,0xC2,0x0C
,0x31,0xC1,0x00,0xCA,0x0D,0xC4,0x3F,0x33,0x3F,0x90,0xC2,0xC4,0x04,0xCA,0x09,0xAA
,0x0F,0x9C,0x06,0xC4,0x00,0xCA,0x0D,0xCA,0x11,0x02,0xC2,0x0D,0xF2,0x0D,0xCA,0x0D
,0xBA,0x09,0x9C,0xF5,0xC2,0x0D,0x58,0xCA,0x0D,0x90,0xDA,0x3F,0x06,0x5B,0x4F,0x66
,0x6D,0x7D,0x07,0x7F,0x67,0x77,0x7C,0x39,0x5E,0x79,0x71,0xC4,0x04,0xCA,0x09,0xAA
,0x0F,0x9C,0x06,0xC4,0x00,0xCA,0x0E,0xCA,0x0C,0x02,0xC2,0x0C,0xF2,0x0C,0xCA,0x0C
,0xC2,0x0E,0xF2,0x0E,0xCA,0x0E,0xBA,0x09,0x9C,0xEF,0xC2,0x0C,0x58,0xCA,0x0C,0x3F
,0xC4,0x01,0x35,0xC4,0x0B,0x31,0xC2,0x0D,0xD4,0x0F,0x01,0xC1,0x80,0xCA,0x00,0xC2
,0x0D,0x1C,0x1C,0x1C,0x1C,0x01,0xC1,0x80,0xCA,0x01,0x03,0xC4,0x01,0x35,0xC4,0x0B
,0x31,0xC2,0x0C,0xD4,0x0F,0x01,0xC1,0x80,0xCA,0x04,0xC2,0x0C,0x1C,0x1C,0x1C,0x1C
,0x01,0xC1,0x80,0xCA,0x05,0x06,0xD4,0x80,0x98,0x09,0x02,0xC4,0x00,0xCA,0x03,0xC6
,0x02,0x90,0xDE,0xC6,0xFE,0xC4,0x00,0xCA,0x0B,0xC4,0x0D,0x35,0xC4,0xFF,0xCA,0x10
,0xC4,0x0A,0xCA,0x09,0xC4,0x00,0xCA,0x0A,0x31,0xAA,0x10,0x01,0xC2,0x80,0xC9,0x80
,0x8F,0x00,0xC1,0x80,0xE4,0xFF,0x9C,0x4C,0xBA,0x09,0x9C,0xED,0xC2,0x0A,0x98,0x0A
,0xC2,0x0B,0x9C,0xD8,0xC2,0x0A,0xCA,0x0B,0x90,0xD2,0xC2,0x0B,0x98,0xCE,0x01,0x40
,0xD4,0x20,0x9C,0x28,0xC4,0x80,0x50,0x9C,0x1B,0xC4,0x40,0x50,0x9C,0x19,0xC4,0x0F
,0x50,0xF4,0x07,0x01,0xC0,0x80,0x01,0xC7,0x02,0x3F,0x90,0xA9,0x0A,0x0B,0x0C,0x0D
,0x00,0x00,0x0E,0x0F,0x60,0x90,0xEF,0x60,0xF4,0x08,0x90,0xEA,0x60,0xE4,0x04,0x98
,0x08,0x3F,0x90,0x91,0x58,0xCA,0x0A,0x90,0xAF,0xC4,0x00,0x37,0xC4,0x3F,0x33,0x3F
};

	/********************************************************************/
	/*						Load in the SCIOS ROM						*/
	/********************************************************************/	
	int readROM( int n, int version ){
		int c;
	if ( version == 0 ){	
		c = (int)ROMImageV1[n];				/* and it is mirrored ! */
	} else  { 
		c = (int)ROMImageV2[n];				/* and it is mirrored ! */
	}
	  return ( c = c & 0xFF );
	}
}


