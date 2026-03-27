@import com.puppycrawl.tools.checkstyle.api.AuditEvent
@import java.io.File
@import java.util.List
@import static io.spring.javaformat.cli.check.RenderHelper.checkstyleHeader
@import static io.spring.javaformat.cli.check.RenderHelper.styledLineNumber
@import static io.spring.javaformat.cli.check.RenderHelper.styledMessage
@import static io.spring.javaformat.cli.check.RenderHelper.styledPath
@param List<AuditEvent> violations
${checkstyleHeader(violations)}
@for(AuditEvent violation : violations)  ${styledPath(new File(violation.getFileName()))}:${styledLineNumber(violation.getLine())}: ${styledMessage(violation.getMessage())}
@endfor