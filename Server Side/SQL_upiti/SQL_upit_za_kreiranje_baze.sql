USE [master]
GO

/****** Object:  Database [Fajlovi]    Script Date: 28.06.22 13:03:03 ******/
CREATE DATABASE [Fajlovi]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'Fajlovi', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL15.MSSQLSERVER\MSSQL\DATA\Fajlovi.mdf' , SIZE = 73728KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'Fajlovi_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL15.MSSQLSERVER\MSSQL\DATA\Fajlovi_log.ldf' , SIZE = 73728KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT
GO

IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [Fajlovi].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO

ALTER DATABASE [Fajlovi] SET ANSI_NULL_DEFAULT OFF 
GO

ALTER DATABASE [Fajlovi] SET ANSI_NULLS OFF 
GO

ALTER DATABASE [Fajlovi] SET ANSI_PADDING OFF 
GO

ALTER DATABASE [Fajlovi] SET ANSI_WARNINGS OFF 
GO

ALTER DATABASE [Fajlovi] SET ARITHABORT OFF 
GO

ALTER DATABASE [Fajlovi] SET AUTO_CLOSE OFF 
GO

ALTER DATABASE [Fajlovi] SET AUTO_SHRINK OFF 
GO

ALTER DATABASE [Fajlovi] SET AUTO_UPDATE_STATISTICS ON 
GO

ALTER DATABASE [Fajlovi] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO

ALTER DATABASE [Fajlovi] SET CURSOR_DEFAULT  GLOBAL 
GO

ALTER DATABASE [Fajlovi] SET CONCAT_NULL_YIELDS_NULL OFF 
GO

ALTER DATABASE [Fajlovi] SET NUMERIC_ROUNDABORT OFF 
GO

ALTER DATABASE [Fajlovi] SET QUOTED_IDENTIFIER OFF 
GO

ALTER DATABASE [Fajlovi] SET RECURSIVE_TRIGGERS OFF 
GO

ALTER DATABASE [Fajlovi] SET  DISABLE_BROKER 
GO

ALTER DATABASE [Fajlovi] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO

ALTER DATABASE [Fajlovi] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO

ALTER DATABASE [Fajlovi] SET TRUSTWORTHY OFF 
GO

ALTER DATABASE [Fajlovi] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO

ALTER DATABASE [Fajlovi] SET PARAMETERIZATION SIMPLE 
GO

ALTER DATABASE [Fajlovi] SET READ_COMMITTED_SNAPSHOT ON 
GO

ALTER DATABASE [Fajlovi] SET HONOR_BROKER_PRIORITY OFF 
GO

ALTER DATABASE [Fajlovi] SET RECOVERY FULL 
GO

ALTER DATABASE [Fajlovi] SET  MULTI_USER 
GO

ALTER DATABASE [Fajlovi] SET PAGE_VERIFY CHECKSUM  
GO

ALTER DATABASE [Fajlovi] SET DB_CHAINING OFF 
GO

ALTER DATABASE [Fajlovi] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO

ALTER DATABASE [Fajlovi] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO

ALTER DATABASE [Fajlovi] SET DELAYED_DURABILITY = DISABLED 
GO

ALTER DATABASE [Fajlovi] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO

ALTER DATABASE [Fajlovi] SET QUERY_STORE = OFF
GO

ALTER DATABASE [Fajlovi] SET  READ_WRITE 
GO

