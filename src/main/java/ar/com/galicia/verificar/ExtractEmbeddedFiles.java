package ar.com.galicia.verificar;/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import ar.com.galicia.log.Logear;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationFileAttachment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is an example on how to extract all embedded files from a PDF document.
 *
 */
public final class ExtractEmbeddedFiles
{

    private String pdfHijo="";
    public ExtractEmbeddedFiles(String pdfHijo){
        this.pdfHijo=pdfHijo;
    }


    public boolean extraerAdjuntos(String pdfPadre) throws IOException {

        boolean response=false;
        PDDocument document = null;
        try
        {
            File pdfFile = new File(pdfPadre);
            String filePath = pdfFile.getParent() + System.getProperty("file.separator");
            document = PDDocument.load(pdfFile );
            PDDocumentNameDictionary namesDictionary =
                    new PDDocumentNameDictionary( document.getDocumentCatalog() );
            PDEmbeddedFilesNameTreeNode efTree = namesDictionary.getEmbeddedFiles();
            if (efTree != null)
            {

                response=true;
                Map<String, PDComplexFileSpecification> names = efTree.getNames();
                if (names != null)
                {
                    extractFiles(names, filePath);
                }
                else
                {
                    List<PDNameTreeNode<PDComplexFileSpecification>> kids = efTree.getKids();
                    for (PDNameTreeNode<PDComplexFileSpecification> node : kids)
                    {
                        names = node.getNames();
                        extractFiles(names, filePath);
                    }
                }
            }else{
                response=false;
                Logear.logEmpresasSAS_debug("Sin adjuntos");
            }

            // extract files from annotations
            for (PDPage page : document.getPages())
            {
                for (PDAnnotation annotation : page.getAnnotations())
                {
                    if (annotation instanceof PDAnnotationFileAttachment)
                    {
                        PDAnnotationFileAttachment annotationFileAttachment = (PDAnnotationFileAttachment) annotation;
                        PDComplexFileSpecification fileSpec = (PDComplexFileSpecification) annotationFileAttachment.getFile();
                        PDEmbeddedFile embeddedFile = getEmbeddedFile(fileSpec);
                        extractFile(filePath, fileSpec.getFilename(), embeddedFile);
                    }
                }
            }

        }
        finally
        {
            if( document != null )
            {
                document.close();
            }
        }

        return response;

    }

    private void extractFiles(Map<String, PDComplexFileSpecification> names, String filePath)
            throws IOException
    {
        for (Entry<String, PDComplexFileSpecification> entry : names.entrySet())
        {
            String filename = entry.getKey();
            PDComplexFileSpecification fileSpec = entry.getValue();
            PDEmbeddedFile embeddedFile = getEmbeddedFile(fileSpec);
            extractFile(filePath, filename, embeddedFile);
        }
    }

    private void extractFile(String filePath, String filename, PDEmbeddedFile embeddedFile)
            throws IOException
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssss");
        String date = sdf.format(new Date());
        String embeddedFilename = filePath + filename;
        //File file = new File(filePath + filename+date+".pdf");

        File file = new File(pdfHijo);
        System.out.println("Writing " + embeddedFilename);
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(embeddedFile.toByteArray());
        }catch (Exception e){

        }
    }

    private static PDEmbeddedFile getEmbeddedFile(PDComplexFileSpecification fileSpec )
    {
        // search for the first available alternative of the embedded file
        PDEmbeddedFile embeddedFile = null;
        if (fileSpec != null)
        {
            embeddedFile = fileSpec.getEmbeddedFileUnicode();
            if (embeddedFile == null)
            {
                embeddedFile = fileSpec.getEmbeddedFileDos();
            }
            if (embeddedFile == null)
            {
                embeddedFile = fileSpec.getEmbeddedFileMac();
            }
            if (embeddedFile == null)
            {
                embeddedFile = fileSpec.getEmbeddedFileUnix();
            }
            if (embeddedFile == null)
            {
                embeddedFile = fileSpec.getEmbeddedFile();
            }
        }
        return embeddedFile;
    }

    /**
     * This will print the usage for this program.
     */
    private static void usage()
    {
        System.err.println( "Usage: java " + ExtractEmbeddedFiles.class.getName() + " <input-pdf>" );
    }
}