USE [Fajlovi]
GO

/****** Object:  Table [dbo].[Artikli]    Script Date: 28.06.22 13:03:57 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[Artikli](
	[ID_Artikli] [int] NOT NULL,
	[ID_Tarifa] [int] NOT NULL,
	[ID_TarifaPoreza] [int] NOT NULL,
	[ID_JedinicaMere] [int] NOT NULL,
	[ID_ArtikliStablo] [int] NOT NULL,
	[NazivArtikla] [nvarchar](500) NOT NULL,
	[StaraSifra] [float] NOT NULL,
	[CCopy] [int] NULL,
	[UUser] [int] NULL,
	[TTime] [datetime] NULL,
	[OBRADA] [smalldatetime] NULL,
	[ID_KadrovskaEvidencija] [int] NULL,
	[ID_AmortizacionaGrupa] [int] NOT NULL,
	[PrivremenaStaraSifra] [nvarchar](50) NULL,
	[TrgovackiNaziv] [nvarchar](250) NULL,
	[Web_Prikaz] [int] NULL,
	[NazivArtEngleski] [nvarchar](100) NOT NULL,
	[Izmenjeno] [nchar](1) NULL,
	[ID_ArtikliGrupe] [int] NOT NULL,
	[ExterniBarkod] [nvarchar](13) NULL,
	[FMSifra] [nvarchar](50) NOT NULL,
	[ID_Pakovanje] [int] NOT NULL,
 CONSTRAINT [PK_Artikli_1] PRIMARY KEY CLUSTERED 
(
	[ID_Artikli] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO


