@import com.puppycrawl.tools.checkstyle.api.AuditEvent
@import java.io.File
@import java.util.List
@import static io.spring.javaformat.cli.check.RenderHelper.combinedHeader
@import static io.spring.javaformat.cli.check.RenderHelper.combinedLines
@param List<File> formattingProblems
@param List<AuditEvent> checkstyleViolations
!{List<String> lines = combinedLines(formattingProblems, checkstyleViolations);}
${combinedHeader(lines.size())}
@for(String line : lines)  ${line}
@endfor