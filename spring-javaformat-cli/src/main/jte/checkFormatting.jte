@import java.io.File
@import java.util.List
@import static io.spring.javaformat.cli.check.RenderHelper.formattingHeader
@import static io.spring.javaformat.cli.check.RenderHelper.styledPath
@param List<File> files
${formattingHeader(files.size())}
@for(File file : files)  ${styledPath(file)}
@endfor