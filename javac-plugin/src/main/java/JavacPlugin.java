import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.VariableTree;
import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;
import com.sun.source.util.TaskEvent;
import com.sun.source.util.TaskListener;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

public class JavacPlugin implements Plugin {

  public String getName() {
    return "ExamplePlugin";
  }

  public void init(JavacTask task, String... strings) {
    task.addTaskListener(new PostAnalyzeTaskListener(task));
  }

  static class PostAnalyzeTaskListener implements TaskListener {
    private final ShowTypeTreeVisitor visitor;

    PostAnalyzeTaskListener(JavacTask task) {
      visitor = new ShowTypeTreeVisitor(task);
    }

    @Override
    public void started(TaskEvent taskEvent) {
    }

    @Override
    public void finished(TaskEvent taskEvent) {
      if (taskEvent.getKind().equals(TaskEvent.Kind.ANALYZE)) {
        CompilationUnitTree compilationUnit = taskEvent.getCompilationUnit();
        visitor.scan(compilationUnit, null);
      }
    }

    static class ShowTypeTreeVisitor extends TreePathScanner<Void, Void> {
      private final Trees trees;
      private CompilationUnitTree currentCompilationUnit;

      ShowTypeTreeVisitor(JavacTask task) {
        trees = Trees.instance(task);
      }

      @Override
      public Void visitCompilationUnit(CompilationUnitTree tree, Void aVoid) {
        currentCompilationUnit = tree;
        return super.visitCompilationUnit(tree, aVoid);
      }

      @Override
      public Void visitVariable(VariableTree tree, Void aVoid) {
        TypeMirror type = trees.getTypeMirror(getCurrentPath());
        trees.printMessage(Diagnostic.Kind.NOTE, "type is " + type, tree, currentCompilationUnit);
        return super.visitVariable(tree, aVoid);
      }
    }
  }

}
