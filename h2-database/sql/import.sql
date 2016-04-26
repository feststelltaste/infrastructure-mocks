SET MODE DB2;

CREATE TABLE TB_SAMPLE (                                               
   LOCATION    DECIMAL(5) NOT NULL                        
  ,CHANGEDTS   TIMESTAMP NOT NULL            
  ,DB2_USERID  CHAR(8) NOT NULL       
  ,POSNR       DECIMAL(1) NOT NULL                 
  ,ZIP         CHAR(8) NOT NULL        
  ,SUBCODE     CHAR(10)                           
  ,LOCNAME     VARCHAR(28) NOT NULL                   
  ,LOCFRACTION VARCHAR(28) NOT NULL  
  ,STREET      VARCHAR(28) NOT NULL                   
  ,VERSION     SMALLINT NOT NULL        
);
