USE [Fajlovi]
GO

/****** Object:  View [dbo].[ArtikliZaSlike]    Script Date: 28.06.22 13:04:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE VIEW [dbo].[ArtikliZaSlike]
AS
SELECT ID_Artikli AS Id, NazivArtikla AS Naziv
FROM     dbo.Artikli
GO
